package net.wilmens.spoppin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class LoginActivity extends BaseSpoppinActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		String APP_ID = getString(R.string.facebook_app_id);
		Session session = Session.getActiveSession();

		// check fb session
		if (session == null) {
			session = new Session(this);
		}
		Session.setActiveSession(session);

		ImageView btnLogin;
		final TextView txtName;

		btnLogin = (ImageView) this.findViewById(R.id.login);
		txtName = (TextView) this.findViewById(R.id.welcome);

		ImageView btnLoginFacebook = (ImageView) findViewById(R.id.login);
		btnLoginFacebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Toast.makeText(MainActivity.this, "welcome",
				// Toast.LENGTH_LONG).show();				
				Session.openActiveSession(LoginActivity.this, true,
						new Session.StatusCallback() {

							@Override
							public void call(Session session,
									SessionState state, Exception exception) {
								// TODO Auto-generated method stub
								if (session.isOpened()) {
									// Toast.makeText(MainActivity.this,
									// "welcome", Toast.LENGTH_LONG).show();
									Request.newMeRequest(session,
											new GraphUserCallback() {

												@Override
												public void onCompleted(GraphUser user, Response response) {
													txtName.setText(user.getName());
													Toast.makeText(LoginActivity.this,"username = " + user.getName(),Toast.LENGTH_LONG).show();
												}
											}).executeAsync();
								}
							}
						});
			}
		});
	}
	
	@Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Session.getActiveSession()
	          .onActivityResult(this, requestCode, resultCode, data);
	  }
}
