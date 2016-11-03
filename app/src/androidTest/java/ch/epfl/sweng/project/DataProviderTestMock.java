package ch.epfl.sweng.project;

/**
 * Created by nathan on 03.11.16.
 */



import static org.hamcrest.MatcherAssert.assertThat;
        import static org.hamcrest.CoreMatchers.*;
        import static org.mockito.Mockito.*;
        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.mockito.Mock;
        import org.mockito.runners.MockitoJUnitRunner;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@RunWith(MockitoJUnitRunner.class)
public class DataProviderTestMock {

    private static final String FAKE_KEY = "fake_key";

    @Mock
    DatabaseReference mDBRef;

    @Test
    public void testPush() {
        // Given a mocked Context injected into the object under test...

        FirebaseDatabase.getInstance().getReference();

        when(mDBRef.child("activities").push().getKey())
                .thenReturn(FAKE_KEY);

    }
}