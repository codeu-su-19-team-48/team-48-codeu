/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.*;

/**
 * Provides access to the data stored in Datastore.
 */
public class Datastore {

    private DatastoreService datastore;

    public Datastore() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    /**
     * Stores the Message in Datastore.
     */
    public void storeMessage(Message message) {
        Entity messageEntity = new Entity("Message", message.getId().toString());
        messageEntity.setProperty("user", message.getUser());
        messageEntity.setProperty("text", message.getText());
        messageEntity.setProperty("timestamp", message.getTimestamp());
        messageEntity.setProperty("skill", message.getskill());
        messageEntity.setProperty("skillLevel", message.getskillLevel());
        messageEntity.setProperty("likes", message.getLikes());

        datastore.put(messageEntity);
    }

    /**
     * Stores the Message in Datastore.
     */
    public Message getSingleMessage(UUID id) {

        Key msgKey = KeyFactory.createKey("Message", id.toString());
        try {
            Entity messageEntity = datastore.get(msgKey);
            String userEmail = (String) messageEntity.getProperty("user");
            String text = (String) messageEntity.getProperty("text");
            long timestamp = (long) messageEntity.getProperty("timestamp");
            String skill =(String) messageEntity.getProperty("skill");
            String skillLevel =(String) messageEntity.getProperty("skillLevel");
            long likes = (long) messageEntity.getProperty("likes");
            Message message = new Message(id, userEmail, text, timestamp,skill, skillLevel,likes);
            return message;
        } catch (Exception e) {
            System.err.println("Entity not found exception.");
            e.printStackTrace();
            return new Message("", "");
        }
    }
    /**
     * Updates message like by id
     *
     * @return a messages with the same id.
     */
    public void updateLikes(UUID id, long likes) {
        Key msgKey = KeyFactory.createKey("Message", id.toString());
        try {
            Entity messageEntity = datastore.get(msgKey);
            messageEntity.setProperty("likes", likes);
            datastore.put(messageEntity);
        } catch (Exception e) {
            System.err.println("Entity not found exception.");
            e.printStackTrace();
        }
    }


    /**
     * Gets messages posted by a specific user.
     *
     * @return a list of messages posted by the user, or empty list if user has never posted a
     * message. List is sorted by time descending.
     */
    public List<Message> getMessages(String userEmail) {
        List<Message> messages = new ArrayList<>();

        Query query =
                new Query("Message")
                        .setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, userEmail))
                        .addSort("timestamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);

        for (Entity entity : results.asIterable()) {
            try {
                String idString = entity.getKey().getName();
                UUID id = UUID.fromString(idString);
                String text = (String) entity.getProperty("text");
                long timestamp = (long) entity.getProperty("timestamp");
                String skill =(String) entity.getProperty("skill");
                String skillLevel =(String) entity.getProperty("skillLevel");
                long likes = (long) entity.getProperty("likes");
                Message message = new Message(id, userEmail, text, timestamp,skill, skillLevel, likes);
                messages.add(message);
            } catch (Exception e) {
                System.err.println("Error reading message.");
                System.err.println(entity.toString());
                e.printStackTrace();
            }
        }

        return messages;
    }

    /**
     * Gets all messages.
     *
     * @return a list of messages posted by the user, or empty list if user has never posted a
     * message. List is sorted by time descending.
     */
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();

        Query query = new Query("Message")
                .addSort("timestamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);

        for (Entity entity : results.asIterable()) {
            try {
                String idString = entity.getKey().getName();
                UUID id = UUID.fromString(idString);
                String userEmail = (String) entity.getProperty("user");
                String text = (String) entity.getProperty("text");
                long timestamp = (long) entity.getProperty("timestamp");
                String skill =(String) entity.getProperty("skill");
                String skillLevel =(String) entity.getProperty("skillLevel");
                long likes = (long) entity.getProperty("likes");
                Message message = new Message(id, userEmail, text, timestamp,skill, skillLevel,likes);
                messages.add(message);
            } catch (Exception e) {
                System.err.println("Error reading message.");
                System.err.println(entity.toString());
                e.printStackTrace();
            }
        }

        return messages;
    }

    /**
     * Returns the total number of messages for all users.
     */
    public int getTotalMessageCount() {
        Query query = new Query("Message");
        PreparedQuery results = datastore.prepare(query);
        return results.countEntities(FetchOptions.Builder.withLimit(1000));
    }

    public Set<String> getUsers() {
        Set<String> users = new HashSet<>();
        Query query = new Query("Message");
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()) {
            users.add((String) entity.getProperty("user"));
        }
        return users;
    }

    /**
     * Stores the User in Datastore.
     */
    public void storeUser(User user) {
        Entity userEntity = new Entity("User", user.getEmail());
        userEntity.setProperty("email", user.getEmail());
        userEntity.setProperty("aboutMe", user.getAboutMe());
        userEntity.setProperty("learnCategory", user.getLearnCategory());
        userEntity.setProperty("teachCategory", user.getTeachCategory());
        userEntity.setProperty("skillLevel", user.getSkillLevel());
        userEntity.setProperty("school", user.getSchool());
        userEntity.setProperty("name", user.getName());
        userEntity.setProperty("age", user.getAge());
        userEntity.setProperty("profilePicUrl", user.getProfilePicUrl());
        //userEntity.setProperty("age", user.getAge());
        if (user.getLikedMessages() == null){
            HashSet<String> newSet = new HashSet<String>();
            newSet.add("  ");
            userEntity.setProperty("likedMessages", newSet);
        }
        else {
            userEntity.setProperty("likedMessages", user.getLikedMessages());
        }
        datastore.put(userEntity);
    }

    /**
     * Returns the User owned by the email address, or
     * null if no matching User was found.
     */
    public User getUser(String email) {

        Query query = new Query("User")
                .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
        PreparedQuery results = datastore.prepare(query);
        Entity userEntity = results.asSingleEntity();
        if (userEntity == null) {
            return null;
        }

        String aboutMe = (String) userEntity.getProperty("aboutMe");
        String learnCategory = (String) userEntity.getProperty("learnCategory");
        String teachCategory = (String) userEntity.getProperty("teachCategory");
        String school = (String) userEntity.getProperty("school");
        String age = (String) userEntity.getProperty("age");
        String name = (String) userEntity.getProperty("name");
        String skillLevel = (String) userEntity.getProperty("skillLevel");
        String profilePicUrl = (String) userEntity.getProperty("profilePicUrl");
        if (userEntity.getProperty("likedMessages") == null){
            User user = new User(email, aboutMe, learnCategory, teachCategory,
                    school, age, name, skillLevel, profilePicUrl);
            return user;
        }
        else {
            HashSet<String> likedMessages = new HashSet<String>();
            likedMessages.addAll((ArrayList<String>)userEntity.getProperty("likedMessages"));
            User user = new User(email, aboutMe, learnCategory, teachCategory,
                    school, age, name, skillLevel, profilePicUrl, likedMessages);
            return user;
        }
    }

    /**
     * Returns users with the same teaching skill, or
     * null if no matching User was found.
     */
    public Set<User> getSkilledUsers(String skill) {
        Set<User> users = new HashSet<>();
        Query query = new Query("User").setFilter(new Query.FilterPredicate("teachCategory",
                FilterOperator.EQUAL, skill));
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()) {

            String email = (String) entity.getProperty("email");
            String aboutMe = (String) entity.getProperty("aboutMe");
            String learnCategory = (String) entity.getProperty("learnCategory");
            String school = (String) entity.getProperty("school");
            String age = (String) entity.getProperty("age");
            String name = (String) entity.getProperty("name");
            String skillLevel = (String) entity.getProperty("skillLevel");
            String profilePicUrl = (String) entity.getProperty("profilePicUrl");
            User user = new User(email, aboutMe, learnCategory, skill,
                    school, age, name, skillLevel, profilePicUrl);

            users.add(user);
        }
        return users;
    }

    /**
     * Returns all users' name and email
     */
    public HashSet<HashMap<String, String>> getAllUsers() {

        HashSet<HashMap<String, String>> users = new HashSet<HashMap<String, String>>();
        Query query = new Query("User");
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()) {

            String email = (String) entity.getProperty("email");
            String name = (String) entity.getProperty("name");
            HashMap<String, String> tmp = new HashMap<>();
            tmp.put("name", name);
            tmp.put("email", email);
            users.add(tmp);
        }
        return users;
    }

    /**
     * Returns teach & learn category num
     */
    public List<HashMap <String, Integer> > getSkillsUserNum() {

        String[] skillTypes = {"Design", "Culinary", "Music", "Sports", "Photography", "Technology", "Language"};
        HashMap<String, Integer> learnUserNum = new HashMap<>();
        HashMap<String, Integer> shareUserNum = new HashMap<>();
        Query query = new Query("User");
        PreparedQuery results = datastore.prepare(query);
        for (int i = 0; i < skillTypes.length; i++) {
            learnUserNum.put(skillTypes[i], 0);
            shareUserNum.put(skillTypes[i], 0);
        }
        for (Entity entity : results.asIterable()) {

            String learn = (String) entity.getProperty("learnCategory");
            String share = (String) entity.getProperty("teachCategory");
            learnUserNum.put(learn, learnUserNum.get(learn) + 1);
            shareUserNum.put(share, shareUserNum.get(share) + 1);
        }
        List<HashMap<String, Integer> > arr = new ArrayList<>();
        arr.add(learnUserNum);
        arr.add(shareUserNum);
        return arr;
    }
}