package ch.epfl.sweng.project;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
public class ConfirmationCodesTest {

    @Test
    public void CodesRetrivalWorks() {

        Context context = InstrumentationRegistry.getTargetContext();

        assertThat(ConfirmationCodes.get_success(context), is(context.getResources().getString(R.string.create_activity_confirmation_message)));
        assertThat(ConfirmationCodes.get_missing_field_error(context), is(context.getResources().getString(R.string.create_activity_missing_field_error_message)));
        assertThat(ConfirmationCodes.get_date_error(context), is(context.getResources().getString(R.string.create_activity_date_error_message)));
        assertThat(ConfirmationCodes.get_missing_location_error(context), is(context.getResources().getString(R.string.create_activity_location_error_message)));
        assertThat(ConfirmationCodes.get_unknown_error(context), is(context.getResources().getString(R.string.create_activity_unknown_error_message)));
    }
}
