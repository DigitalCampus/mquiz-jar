package org.digitalcampus.mquiz;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.digitalcampus.mquiz.model.QuizQuestion;
import org.digitalcampus.mquiz.model.Response;
import org.digitalcampus.mquiz.model.questiontypes.Essay;
import org.digitalcampus.mquiz.model.questiontypes.Info;
import org.digitalcampus.mquiz.model.questiontypes.Matching;
import org.digitalcampus.mquiz.model.questiontypes.MultiChoice;
import org.digitalcampus.mquiz.model.questiontypes.MultiSelect;
import org.digitalcampus.mquiz.model.questiontypes.Numerical;
import org.digitalcampus.mquiz.model.questiontypes.ShortAnswer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MQuiz implements Serializable {

	public static final String TAG = "MQuiz";
	
	public static final String RESPONSE_SEPARATOR = "||";
	public static final String MATCHING_SEPARATOR = "|";
	public static final String MATCHING_REGEX = "\\|";
	
	private static final long serialVersionUID = -2416034891439585524L;
	private String id;
	private String title;
	private String url;
	private float maxscore;
	private boolean checked;
	private int currentq = 0;
	private float userscore;
	private List<QuizQuestion> questions = new ArrayList<QuizQuestion>();

	public MQuiz() {
	}

	public boolean load(String quiz) {
		try {
			JSONObject json = new JSONObject(quiz);
			this.id = (String) json.get("id");
			this.title = (String) json.get("title");
			JSONObject props = json.getJSONObject("props");
			this.maxscore = props.getLong("maxscore");
			
			int randomSelect = 0;
			try {
				randomSelect = props.getInt("randomselect");
			} catch (JSONException e) {
				
			}
			
			// add questions
			JSONArray questions = (JSONArray) json.get("questions");
			if (randomSelect > 0){
				this.generateQuestionSet(questions, randomSelect);
			} else {
				for (int i = 0; i < questions.length(); i++) {
					this.addQuestion((JSONObject) questions.get(i));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void generateQuestionSet(JSONArray questionChoices, int randomSelect){
		Random generator = new Random();
		while(this.questions.size() < randomSelect){
			int randomNum = generator.nextInt(questionChoices.length());
			boolean found = false;
			JSONObject quizquestion;
			try {
				quizquestion = (JSONObject) questionChoices.get(randomNum);
				JSONObject q = quizquestion.getJSONObject("question");
				int qid = q.getInt("id");
				for(int i=0; i < this.questions.size(); i++){
					if (qid == this.questions.get(i).getID()){
						found = true;
					}
				}
				if(!found){
					this.addQuestion(quizquestion);
				}
			} catch (JSONException e) {
				
			}
		}
		
		// now set the new maxscore
		float newMax = 0;
		for(int i=0; i<this.questions.size(); i++){
			newMax += this.questions.get(i).getMaxScore();
		}
		this.maxscore = newMax;
	}
	
	private boolean addQuestion(JSONObject qObj) {
		
		// determine question type
		QuizQuestion question;
		String qtype;
		try {
			JSONObject q = qObj.getJSONObject("question");
			qtype = (String) q.get("type");
			if (qtype.toLowerCase().equals(Essay.TAG.toLowerCase())) {
				question = new Essay();
			} else if (qtype.toLowerCase().equals(MultiChoice.TAG.toLowerCase())) {
				question = new MultiChoice();
			} else if (qtype.toLowerCase().equals(Numerical.TAG.toLowerCase())) {
				question = new Numerical();
			} else if (qtype.toLowerCase().equals(Matching.TAG.toLowerCase())) {
				question = new Matching();
			} else if (qtype.toLowerCase().equals(ShortAnswer.TAG.toLowerCase())) {
				question = new ShortAnswer();
			} else if (qtype.toLowerCase().equals(MultiSelect.TAG.toLowerCase())) {
					question = new MultiSelect();
			} else if (qtype.toLowerCase().equals(Info.TAG.toLowerCase())) {
				question = new Info();
			} else {
				Log.d(TAG, "Question type " + qtype + " is not yet supported");
				return false;
			}

			question.setID(q.getInt("id"));
			question.setTitle((String) q.get("title"));
			JSONObject questionProps = (JSONObject) q.get("props");

			HashMap<String, String> qProps = new HashMap<String, String>();
			for (int k = 0; k < questionProps.names().length(); k++) {
				qProps.put(questionProps.names().getString(k),
						questionProps.getString(questionProps.names().getString(k)));
			}
			question.setProps(qProps);

			this.questions.add(question);

			// now add response options for this question
			JSONArray responses = (JSONArray) q.get("responses");
			for (int j = 0; j < responses.length(); j++) {
				JSONObject r = (JSONObject) responses.get(j);
				Response responseOption = new Response();
				responseOption.setTitle((String) r.get("title"));
				responseOption.setScore(Float.parseFloat((String) r.get("score")));
				JSONObject responseProps = (JSONObject) r.get("props");
				HashMap<String, String> rProps = new HashMap<String, String>();
				if (responseProps.names() != null) {
					for (int m = 0; m < responseProps.names().length(); m++) {
						rProps.put(responseProps.names().getString(m),
								responseProps.getString(responseProps.names().getString(m)));
					}
				}
				responseOption.setProps(rProps);
				question.addResponseOption(responseOption);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean hasNext() {
		if (this.currentq + 1 < questions.size()) {
			return true;
		}
		return false;
	}

	public boolean hasPrevious() {
		if (this.currentq > 0) {
			return true;
		}
		return false;
	}

	public void moveNext() {
		if (currentq + 1 < questions.size()) {
			currentq++;
		}
	}

	public void movePrevious() {
		if (currentq > 0) {
			currentq--;
		}
	}

	public void mark() {
		float total = 0;
		for (QuizQuestion q : questions) {
			q.mark();
			total += q.getUserscore();
		}
		if (total > maxscore) {
			userscore = maxscore;
		} else {
			userscore = total;
		}
	}

	public String getID() {
		return this.id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String t) {
		this.title = t;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getCurrentQuestionNo() {
		return this.currentq + 1;
	}

	public QuizQuestion getCurrentQuestion() {
		return questions.get(this.currentq);
	}

	public float getUserscore() {
		return this.userscore;
	}

	public float getMaxscore() {
		return maxscore;
	}

	public void setMaxscore(float maxscore) {
		this.maxscore = maxscore;
	}

	public int getTotalNoQuestions() {
		return questions.size();
	}

	public JSONObject getResultObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("quiz_id", this.getID());
			Date now = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			json.put("attempt_date", simpleDateFormat.format(now));
			json.put("score", this.getUserscore());
			json.put("maxscore", this.getMaxscore());
			JSONArray responses = new JSONArray();
			for(QuizQuestion q: questions){
				JSONObject r = q.responsesToJSON();
				responses.put(r);
			}
			json.put("responses", responses);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

}
