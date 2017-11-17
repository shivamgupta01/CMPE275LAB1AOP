package edu.sjsu.cmpe275.aop.aspect;

import edu.sjsu.cmpe275.aop.exceptions.AccessDeniedExeption;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;


/**
 *
 */
@Aspect
public class AuthorizationAspect
{



    // Check if UserId is Proper Length.
    public static boolean checkUserId(String userId){

        if ((userId.length() <4 || userId.length()>16)) {return false;}
        else { return true; }


    }

    // Share Blog to Other's ,and share blog you have been shared to.
	@After(value = "execution(public * edu.sjsu.cmpe275.aop.BlogService.shareBlog(..))")
	public void validateSharedto(JoinPoint joinPoint) throws AccessDeniedExeption {
		Object[] signatureArgs = joinPoint.getArgs();
		String userId = (String) signatureArgs[0];
		String blogUserId = (String) signatureArgs[1];
		String targetUserId = (String) signatureArgs[2];

        // Check the Validity of UserId
		if(!checkUserId(userId)||!checkUserId(targetUserId)||!checkUserId(blogUserId))
        {
            throw new IllegalArgumentException("Length of UserId is not Correct");
        }

		for (ValidationAspect.Blog blog : ValidationAspect.getAllBlogs()) {
            if (blog.getUserId() == blogUserId) {
                if (blogUserId == targetUserId)
                {
                    System.out.println(targetUserId + ": You already have access to your own Blog.");
                    break;
                }
                if (blog.getSharedWith().contains(userId)) {
                    if (targetUserId != userId) {
                        blog.addSharedWith(targetUserId);
                        //System.out.println(String.format("The %s is already present in the Access List", userId));
                    }
                }
                else {
                    throw new AccessDeniedExeption("The user::"+ userId +" is not Authorized the share the Blog.");
                }
            }
        }
	}



	@Before("execution(public * edu.sjsu.cmpe275.aop.BlogService.unshareBlog(..))")
	public void validateUnsharedTo(JoinPoint joinPoint) throws AccessDeniedExeption {
        Object[] signatureArgs = joinPoint.getArgs();
        String userId = (String) signatureArgs[0];
        String targetUserId = (String) signatureArgs[1];

        if(!checkUserId(userId)||!checkUserId(targetUserId))
        {
            throw new IllegalArgumentException();
        }


        for (ValidationAspect.Blog blog : ValidationAspect.getAllBlogs()){
            if (blog.getUserId()== userId){
                if (blog.getSharedWith().contains(targetUserId)){

                    blog.removeSharedWith(targetUserId);
                }
                // If the User is not shared the blog to Someone, then he is not Authorized to Unshare the Blog in the first place,
                else {
                    throw new AccessDeniedExeption("The user:"+ targetUserId +" is not been shared the Blog in the first place.");
                }
            }

        }
	}




	
}
