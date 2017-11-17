package edu.sjsu.cmpe275.aop.aspect;
import org.aspectj.lang.annotation.Aspect;
import edu.sjsu.cmpe275.aop.exceptions.NetworkException;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;

@Aspect
public class RetryAspect {


    // Around Annotation fro all the Methods and BlogService Interface.
	@Around(value = "execution(public * edu.sjsu.cmpe275.aop.BlogService.*(..))")
	public void retryNetworkError(ProceedingJoinPoint join_point) throws Throwable {

		try {
			join_point.proceed();

		} catch (NetworkException e) {
			try {

				join_point.proceed();

			} catch (NetworkException ex) {

				try {

					join_point.proceed();

				} catch (NetworkException exc) {

					throw exc;
				}
			}
		}
	}
}
