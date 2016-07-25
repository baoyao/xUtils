package butterknife;


import android.view.View.OnClickListener;
/**
 * 
 * @author houen.bao
 *
 */
public @interface Bind{
	int value();

	Class<?> type() default OnClickListener.class;
	
	
}