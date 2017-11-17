package edu.sjsu.cmpe275.aop.aspect;
import edu.sjsu.cmpe275.aop.exceptions.AccessDeniedExeption;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Aspect
public class ValidationAspect
{



                                    /*List of Object from Inner Class Blog.
                                    Data Structure Of Blog is as follows:

                                    userId <String>
                                    Description <String>
                                    Message <ArrayList<String>>
                                    sharedTo <ArrayList<String>>
                                    */
    private static List<Blog> ALL_BLOGS = Arrays.asList(
            new Blog("Alice", Arrays.asList("This a Comment from Alice"), "This blog talks About BoB",
                    Arrays.asList("Alice")),
            new Blog("Bobby", Arrays.asList("This a Comment from Bob"), "This blog talks About BoB",
                    Arrays.asList("Bobby", "Alice")),
            new Blog("Charlie", Arrays.asList("This a Comment from Charlie"),
                    "This blog talks About Charlie", Arrays.asList("Charlie"))
    );



    // Method to get the Blog Repository.
    public static List<Blog> getAllBlogs() {
        return ALL_BLOGS;
    }


    //Method to Check if user have Access to the Requested blog/
    @Before(value = "execution(public * edu.sjsu.cmpe275.aop.BlogService.readBlog(..)) ")
    public void readCheck(JoinPoint joinPoint) throws AccessDeniedExeption
    {

        Object[] signatureArgs = joinPoint.getArgs();
        String userId = (String) signatureArgs[0];
        String blogUserId = (String) signatureArgs[1];



        // Check if UserId is > 4 and <16 Characters.
        if(!AuthorizationAspect.checkUserId(userId)||!AuthorizationAspect.checkUserId(blogUserId))
        {
            throw new IllegalArgumentException();
        }


        // Search through the List of Blog to Find the Blog.
        for (Blog blog : ALL_BLOGS) {
            if (blog.getUserId().equalsIgnoreCase(blogUserId))
            {
                // Check if Users requesting the Blog is same as Blog User, and if the User has Access to the blog.
                if ((!userId.equalsIgnoreCase(blogUserId)) && (!blog.getSharedWith().contains(userId))) {
                    // Throwing Access Denied Exception
                    throw new AccessDeniedExeption(String.format("The Blog is not shared with %s", userId));

                }

            }
        }
    }

    // Validate if Current User is Eligible to Comment on other's Blog.
    @Before(value = "execution(public * edu.sjsu.cmpe275.aop.BlogServiceImpl.commentOnBlog(..))")
    public void addComment(JoinPoint joinPoint) throws AccessDeniedExeption
    {
        Object[] signatureArgs = joinPoint.getArgs();
        String userId = (String) signatureArgs[0];
        String blogUserId =(String) signatureArgs[1];
        String comment = (String) signatureArgs[2];



        // Check Size of Comment
       if ((comment == null) || comment.length()>100)
        {
            //Throwing Exception In case the size of comment is a Problem.
            throw new IllegalArgumentException(String.format("Check Size of Comment Provided: %s", comment));
        }


        // Check if the user have the Permission to comment in the Blog.
        if(!AuthorizationAspect.checkUserId(userId)||!AuthorizationAspect.checkUserId(blogUserId))
        {
            // throwing Exception in case UserId is not correct.
            throw new IllegalArgumentException();
        }

        // Access all the blog information to search fot he Blog.
        for (ValidationAspect.Blog blog: ValidationAspect.getAllBlogs())
        {
            if (blogUserId == blog.getUserId())
            {
                // Check id Current User Have Access to Comment on Other's Blog.
                if ((!blog.getSharedWith().contains(userId)) && (userId != blogUserId))
                {
                    // Throwing Exception
                    throw new AccessDeniedExeption(String.format("The Blog is not shared with %s", userId));
                }
            }
        }
    }


    // Inner Class Blog With details of Blog Object and Getter and Setters.
    public static class Blog {



        private String  userId;
        private List<String > message;
        private String description;
        private List<String> sharedTo;


        public Blog(String userId, List<String> message, String description, List<String> sharedTo) {
            this.sharedTo = new ArrayList<String>();
            this.message = new ArrayList<String>();
            this.userId = userId;
            this.message.addAll(message);
            this.description = description;
            this.sharedTo.addAll(sharedTo);
        }



        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<String> getMessage() {
            return message;
        }

        public void setMessage(String comment)
        {
            message.add(comment);
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getSharedWith() {
            return sharedTo;
        }

        public void addSharedWith(String targetUserId) {
            //System.out.println(" set shared with target user id");
            sharedTo.add(targetUserId);
        }

        public void removeSharedWith(String targetUserId)
        {
            sharedTo.remove(targetUserId);
        }
    }
}