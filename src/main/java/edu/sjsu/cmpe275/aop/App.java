package edu.sjsu.cmpe275.aop;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

public class App {
	public static void main(String[] args) {
        /***
         * Following is a dummy implementation of App to demonstrate bean creation with Application context.
         * You may make changes to suit your need, but this file is NOT part of the submission.
         */

    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml");
        BlogService blogService = (BlogService) ctx.getBean("blogService");

        try {
            blogService.shareBlog("alice", "Bobby", "Charlie");

            //blogService.commentOnBlog("Bob", "Alice", "Nice work!");
        	/*blogService.shareBlog("Alice", "Alice", "Bob");
            blogService.commentOnBlog("Bob", "Alice", "Nice work!");
            blogService.readBlog("Bob", "Alice");
            blogService.unshareBlog("Alice", "Bob");
            blogService.readBlog("Bob", "Alice");*/


        } catch (Exception e) {

            e.printStackTrace();

        }
        ctx.close();
    }


}