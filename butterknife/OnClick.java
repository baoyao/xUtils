package butterknife;

import android.view.View.OnClickListener;
/**
 * 
 * @author houen.bao
 *
 */
public @interface OnClick {
	int[] value();

	Class<?> type() default OnClickListener.class;
}
