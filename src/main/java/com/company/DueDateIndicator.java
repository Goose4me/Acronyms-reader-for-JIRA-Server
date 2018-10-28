package com.company;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.*;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.io.InputStreamReader;
import java.io.Reader;
public class DueDateIndicator extends AbstractJiraContextProvider{
    static LinkedList FindResultAcronyms(String[] WordsFromAllFields, Map AcronymsAndDesriptions){
        LinkedList<String> UsedAcronyms = new LinkedList<String>();
        for (int i = 0; i< WordsFromAllFields.length; i++){
            if (AcronymsAndDesriptions.get(WordsFromAllFields[i])!=null){
                if (UsedAcronyms.indexOf(WordsFromAllFields[i])== -1){
                    UsedAcronyms.add(WordsFromAllFields[i]);
                }
            }
        }
        return UsedAcronyms;
    }
    static Map<String, String> AcronymsAndDesriptions = new HashMap();
    @Override
    public Map<String,Object> getContextMap(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        Map<String, Object> contextMap = new HashMap<>();
        Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");
        String TextFromFields = (currentIssue.getSummary().toUpperCase()+ " " + currentIssue.getDescription().toUpperCase()).replaceAll("[^A-Za-z0-9\\n]", " ").replaceAll("\\s{2,}", " ").trim();
        String[] WordsFromAllFields = new String[TextFromFields.length()];
        WordsFromAllFields = TextFromFields.split(" ");
        LinkedList<String> ResultAcronyms = new LinkedList<>();
        boolean AlreadyRead = false;
        if(!AlreadyRead) {
            AlreadyRead = true;
            try (Reader configReader = new BufferedReader(new InputStreamReader(
                    DueDateIndicator.class.getResourceAsStream("/com/blah/blah/dictionary.txt"), "UTF-8"))) {
                String sCurrentLine;

                while ((sCurrentLine =  ((BufferedReader) configReader).readLine()) != null){
                    AcronymsAndDesriptions.put(sCurrentLine.toUpperCase().split(" - ")[0], sCurrentLine.split(" - ")[1]);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ResultAcronyms = FindResultAcronyms(WordsFromAllFields, AcronymsAndDesriptions);
        LinkedList<String> ResultDescriptions = new LinkedList<>();
        for (int i=0; i<ResultAcronyms.size(); i++) {
                   ResultDescriptions.add(AcronymsAndDesriptions.get(ResultAcronyms.get(i)));
               }       
        contextMap.put("Descriptions",  ResultDescriptions);
        contextMap.put("Acronyms", ResultAcronyms);
        return contextMap;
    }
}           