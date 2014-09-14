package org.digitalcampus.mobile.quiz.model;

import java.io.Serializable;
import java.util.HashMap;

public class Response implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5970350772982572264L;
	public static final String TAG = "Response";
	private HashMap<String,String> title = new HashMap<String,String>();
	private float score;
	private HashMap<String,String> props = new HashMap<String,String>();
	
	public String getTitle(String lang) {
		return title.get(lang);
	}

	public void setTitleForLang(String lang, String title) {
		this.title.put(lang, title);
	}
	
	public float getScore() {
		return score;
	}
	
	public void setScore(float score) {
		this.score = score;
	}

	public void setProps(HashMap<String,String> props) {
		this.props = props;
	}
	
	public String getProp(String key) {
		return props.get(key);
	}	
}
