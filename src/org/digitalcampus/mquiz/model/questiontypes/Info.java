package org.digitalcampus.mquiz.model.questiontypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.digitalcampus.mquiz.model.QuizQuestion;
import org.digitalcampus.mquiz.model.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class Info implements Serializable, QuizQuestion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 809312927290284785L;
	public static final String TAG = "Info";
	
	private int id;
	private float userscore = 0;
	private String title;
	private HashMap<String,String> props = new HashMap<String,String>();
	private List<String> userResponses = new ArrayList<String>();
	
	@Override
	public void addResponseOption(Response r) {
		//do nothing
	}

	@Override
	public List<Response> getResponseOptions() {
		// nothing
		return null;
	}

	@Override
	public void setUserResponses(List<String> str) {
		// nothing
		
	}

	@Override
	public List<String> getUserResponses() {
		// nothing
		return null;
	}

	@Override
	public void setResponseOptions(List<Response> responses) {
		// nothing
		
	}

	@Override
	public void mark() {
		this.userscore = 0;
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
	public String getFeedback() {
		return "";
	}

	@Override
	public int getMaxScore() {
		return 0;
	}

	@Override
	public JSONObject responsesToJSON() {
		JSONObject jo = new JSONObject();
		for(String ur: userResponses ){
			try {
				jo.put("question_id", this.id);
				jo.put("score",userscore);
				jo.put("text", ur);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jo;
	}

}
