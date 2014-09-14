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

public class Matching implements Serializable, QuizQuestion {

	private static final long serialVersionUID = -7500128521011492086L;
	public static final String TAG = "Matching";
	private int id;
	private HashMap<String,String> title = new HashMap<String,String>();
	private List<Response> responseOptions = new ArrayList<Response>();
	private float userscore = 0;
	private List<String> userResponses = new ArrayList<String>();
	private HashMap<String, String> props = new HashMap<String, String>();
	private String feedback = "";
	private boolean feedbackDisplayed = false;

	public void addResponseOption(Response r) {
		responseOptions.add(r);
	}

	public List<Response> getResponseOptions() {
		return responseOptions;
	}

	public void mark() {
		// loop through the responses
		// find whichever are set as selected and add up the responses

		float total = 0;

		for (Response r : responseOptions) {
			for (String ur : userResponses) {
				if (ur.equals(r.getTitle())) {
					total += r.getScore();
				} 
			}

			// fix marking so that if one of the incorrect scores is selected
			// final mark is 0
			for (String ur : userResponses) {
				if (r.getTitle().equals(ur) && r.getScore() == 0) {
					total = 0;
				}
			}
		}
		int maxscore = Integer.parseInt(this.getProp("maxscore"));
		if (total > maxscore) {
			userscore = maxscore;
		} else {
			userscore = total;
		}
	}

	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id;	
	}
	
	@Override
	public String getTitle(String lang) {
		return this.title.get(lang);
	}
	
	@Override
	public void setTitleForLang(String lang, String title) {
		this.title.put(lang, title);
	}

	public void setResponseOptions(List<Response> responses) {
		this.responseOptions = responses;
	}

	public float getUserscore() {
		return this.userscore;
	}

	public void setUserResponses(List<String> str) {
		if (!str.equals(this.userResponses)){
			this.setFeedbackDisplayed(false);
		}
		this.userResponses = str;
	}

	public void setProps(HashMap<String, String> props) {
		this.props = props;
	}

	public String getProp(String key) {
		return props.get(key);
	}

	public List<String> getUserResponses() {
		return this.userResponses;
	}

	public String getFeedback() {
		this.feedback = "";
		this.mark();
		if(this.getScoreAsPercent() >= Quiz.QUIZ_QUESTION_PASS_THRESHOLD 
				&& this.getProp("correctfeedback") != null 
				&& !this.getProp("correctfeedback").equals("")){
			return this.getProp("correctfeedback");
		} else if(this.getScoreAsPercent() == 0
				&& this.getProp("incorrectfeedback") != null 
				&& !this.getProp("incorrectfeedback").equals("")){
			return this.getProp("incorrectfeedback");
		} else if (this.getProp("partiallycorrectfeedback") != null 
				&& !this.getProp("partiallycorrectfeedback").equals("")){
			return this.getProp("partiallycorrectfeedback");
		} else {
			return this.feedback;
		}
	}

	public int getMaxScore() {
		return Integer.parseInt(this.getProp("maxscore"));
	}

	public JSONObject responsesToJSON() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("question_id", this.id);
			jo.put("score", this.getUserscore());
			String qrtext = "";
			for (String ur : userResponses) {
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
