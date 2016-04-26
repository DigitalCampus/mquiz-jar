package org.digitalcampus.oppia.quiz.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.digitalcampus.mobile.quiz.Quiz;
import org.digitalcampus.mobile.quiz.model.QuizQuestion;
import org.digitalcampus.mobile.quiz.model.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class QuizTest {

	private static String DEFAULT_LANG = "en";

	public static void main(String[] args) {
	
		String moduleXML = args[0];

		ArrayList<String> quizzes = new ArrayList<String>();
		try {
			quizzes = readXML(moduleXML);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String quizString: quizzes){
			testQuiz(quizString);
		}
		
	}
	
	// Test quiz
	public static void testQuiz(String quizString){
		Quiz q = new Quiz();
		q.load(quizString, DEFAULT_LANG);
		for(QuizQuestion qq: q.getQuestions()){
			System.out.println("\n====================================================\n");
			System.out.println(qq.getTitle(DEFAULT_LANG));
			System.out.println("\nMaximum score: " + qq.getMaxScore());
			System.out.println("Question type: " + qq.getClass().getSimpleName());
			if(qq.getResponseOptions() != null){
				System.out.println("\nScore | Response Option");
				System.out.println("----------------------------");
				for (Response r: qq.getResponseOptions()){
					List<String> response = new ArrayList<String>();
					response.add(r.getTitle(DEFAULT_LANG));
					qq.setUserResponses(response);
					qq.mark(DEFAULT_LANG);
					System.out.println(qq.getUserscore() + "   | " + r.getTitle(DEFAULT_LANG));
					if (!qq.getFeedback(DEFAULT_LANG).equals("")){
						System.out.println("feedback: " + qq.getFeedback(DEFAULT_LANG));
					} else {
						//System.out.println("No feedback specified for this response");
					}
				}
			}
		}
	}
	
	
	/*
	 * Read the specified XML file to extract all the quizzes from it
	 */
	public static ArrayList<String> readXML(String xmlFile) throws ParserConfigurationException, SAXException, IOException{
		ArrayList<String> quizzes = new ArrayList<String>();
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse (new File(xmlFile));
        // normalize text representation
        doc.getDocumentElement().normalize();
        
        NodeList listOfActivities = doc.getElementsByTagName("activity");
        for(int i=0; i<listOfActivities.getLength() ; i++){
        	Node activityNode = listOfActivities.item(i);
        	Element e = (Element) activityNode;
        	String name = e.getAttribute("type");
        	if (name.equals("quiz")){
                NodeList activityNodeList = e.getChildNodes();
                for(int j=0; j<activityNodeList.getLength() ; j++){
                	Node contentNode = activityNodeList.item(j);
                	if (contentNode.getNodeName().equals("content")){
                		quizzes.add(contentNode.getTextContent());
                	}
                }
        	}
        }
        return quizzes;
	}

}
