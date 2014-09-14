package org.digitalcampus.mobile.quiz.model.questiontypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.digitalcampus.mobile.quiz.model.QuizQuestion;
import org.digitalcampus.mobile.quiz.model.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class Numerical implements Serializable, QuizQuestion {

	private static final long serialVersionUID = 808485823168202643L;
	public static final String TAG = "Numerical";
	private HashMap<String,String> title = new HashMap<String,String>();
	private int id;
	private List<Response> responseOptions = new ArrayList<Response>();
	private float userscore = 0;
	private List<String> userResponses = new ArrayList<String>();
	private HashMap<String, String> props = new HashMap<String, String>();
	private String feedback = "";
	private boolean feedbackDisplayed = false;
	
	@Override
	public void addResponseOption(Response r) {
		responseOptions.add(r);
	}

	@Override
	public List<Response> getResponseOptions() {
		return responseOptions;
	}

	@Override
	public List<String> getUserResponses() {
		return this.userResponses;
	}

	@Override
	public void mark(String lang) {
		Float userAnswer = null;
		this.userscore = 0;
		Iterator<String> itr = this.userResponses.iterator();
		while (itr.hasNext()) {
			String a = itr.next();
			try {
				userAnswer = Float.parseFloat(a);
			} catch (NumberFormatException nfe) {
			}
		}
		float score = 0;
		if (userAnswer != null) {
			float currMax = 0;
			// loop through the valid answers and check against these
			for (Response r : responseOptions) {
				try {
					Float respNumber = Float.parseFloat(r.getTitle(lang));
					Float tolerance = (float) 0.0;
					if(r.getProp("tolerance") != null){
						tolerance = Float.parseFloat(r.getProp("tolerance"));
					}
					 
					if ((respNumber - tolerance <= userAnswer) && (userAnswer <= respNumber + tolerance)) {
						if (r.getScore() > currMax) {
							score = r.getScore();
							currMax = r.getScore();
							if (r.getProp("feedback") != null && !r.getProp("feedback").equals("")){
								this.feedback = r.getProp("feedback");
							}
						}
					}
				} catch (NumberFormatException nfe) {
					// do nothing - just skip over this particular response option
				}
			}
		}

		if (score == 0){
			for (Response r : responseOptions){
				if (r.getTitle(lang).toLowerCase().equals("*")){
					if(r.getProp("feedback") != null && !(r.getProp("feedback").equals(""))){
						this.feedback = r.getProp("feedback");
					}
				}
			}
		}
		
		int maxscore = Integer.parseInt(this.getProp("maxscore"));
		if (score > maxscore) {
			this.userscore = maxscore;
		} else {
			this.userscore = score;
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
	public String getTitle(String lang) {
		return this.title.get(lang);
	}
	
	@Override
	public void setTitleForLang(String lang, String title) {
		this.title.put(lang, title);
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
	public void setProps(HashMap<String, String> props) {
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
	public String getFeedback(String lang) {
		// reset feedback back to nothing
		this.feedback = "";
		this.mark(lang);
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
			jo.put("score", userscore);
			for (String ur : userResponses) {
				jo.put("text", ur);
			}
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
