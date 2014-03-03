package org.digitalcampus.mobile.quiz.model.questiontypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.digitalcampus.mobile.quiz.Quiz;
import org.digitalcampus.mobile.quiz.model.QuizQuestion;
import org.digitalcampus.mobile.quiz.model.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class MultiSelect implements Serializable, QuizQuestion {

	private static final long serialVersionUID = 936284577467681053L;
	public static final String TAG = "MultiSelect";
	private int id;
	private String title;
	private List<Response> responseOptions = new ArrayList<Response>();
	private float userscore = 0;
	private List<String> userResponses = new ArrayList<String>();
	private HashMap<String,String> props = new HashMap<String,String>();
	private String feedback = "";
	private boolean feedbackDisplayed = false;
	
	@Override
	public void addResponseOption(Response r){
		responseOptions.add(r);
	}
	
	@Override
	public List<Response> getResponseOptions(){
		return responseOptions;
	}
	
	@Override
	public void mark(){
		// loop through the responses
		// find whichever are set as selected and add up the responses
		
		float total = 0;
		
		for (Response r : responseOptions){
			for (String ur : userResponses) {
				if (ur.equals(r.getTitle())) {
					total += r.getScore();
					if(r.getProp("feedback") != null && !r.getProp("feedback").equals("")){
						this.feedback += ur + ": " + r.getProp("feedback") + "\n\n";
					}
				}  
			}
			
		}
		
		// fix marking so that if one of the incorrect scores is selected final mark is 0
		for (Response r : responseOptions){
			for(String ur: userResponses){
				if (r.getTitle().equals(ur) && r.getScore() == 0){
					total = 0;
				}
			}
		}
		int maxscore = Integer.parseInt(this.getProp("maxscore"));
		if (total > maxscore){
			userscore = maxscore;
		} else {
			userscore = total;
		}
	}
	
	@Override
	public int getID() {
		return this.id;
	}
	
	@Override
	public void setID(int id) {
		this.id = id;	
	}
	
	@Override
	public String getTitle() {
		return this.title;
	}
	
	@Override
	public void setTitle(String title) {
		this.title = title;	
	}

	@Override
	public void setResponseOptions(List<Response> responses) {
		this.responseOptions = responses;
	}

	@Override
	public float getUserscore() {
		return this.userscore;
	}

	@Override
	public List<String> getUserResponses() {
		return this.userResponses;
	}

	@Override
	public void setProps(HashMap<String,String> props) {
		this.props = props;
	}
	
	@Override
	public String getProp(String key) {
		return props.get(key);
	}

	@Override
	public void setUserResponses(List<String> str) {
		if (!str.equals(this.userResponses)){
			this.setFeedbackDisplayed(false);
		}
		this.userResponses = str;
	}
	
	@Override
	public String getFeedback() {
		// reset feedback back to nothing
		this.feedback = "";
		this.mark();
		return this.feedback;
	}
	
	@Override
	public int getMaxScore() {
		return Integer.parseInt(this.getProp("maxscore"));
	}
	
	@Override
	public JSONObject responsesToJSON() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("question_id", this.id);
			jo.put("score",userscore);
			String qrtext = "";
			for(String ur: userResponses ){
				qrtext += ur + Quiz.RESPONSE_SEPARATOR;
			}
			jo.put("text", qrtext);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo;
	}
	
	@Override
	public boolean responseExpected() {
		return true;
	}

	@Override
	public int getScoreAsPercent() {
		int pc = Integer.valueOf((int) (100* this.getUserscore()))/this.getMaxScore();
		return pc;
	}
	
	@Override
	public void setFeedbackDisplayed(boolean feedbackDisplayed) {
		this.feedbackDisplayed = feedbackDisplayed;
		
	}

	@Override
	public boolean getFeedbackDisplayed() {
		return feedbackDisplayed;
	}
}
