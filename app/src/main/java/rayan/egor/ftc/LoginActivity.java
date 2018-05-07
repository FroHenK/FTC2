package rayan.egor.ftc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static rayan.egor.ftc.RESTConstants.BAD_PASSWORD;
import static rayan.egor.ftc.RESTConstants.BAD_USERNAME;
import static rayan.egor.ftc.RESTConstants.PREF;
import static rayan.egor.ftc.RESTConstants.TOKEN;
import static rayan.egor.ftc.RESTConstants.USERNAME_OCCUPIED;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private EditText usernameView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameView = findViewById(R.id.usernameEditText);
        passwordView = findViewById(R.id.passwordEditText);

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);

        if (getSharedPreferences(PREF, MODE_PRIVATE).contains(TOKEN)) {
            showProgress(true);
            final String token = getSharedPreferences(PREF, MODE_PRIVATE).getString(TOKEN, "Scheiße!");
            VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(new StringRequest(Request.Method.POST, "https://guarded-caverns-89583.herokuapp.com/validate", new Response.Listener<String>() {
                @Override
                public void onResponse(String stringResponse) {
                    try {
                        JSONObject response = new JSONObject(stringResponse);
                        if (response.getString(RESTConstants.STATUS).equals(RESTConstants.SUCCESS)) {
                            onSuccessfulLogin();
                        } else {
                            getSharedPreferences(PREF, MODE_PRIVATE).edit().remove(TOKEN).commit();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    showProgress(false);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, R.string.check_internet_connection, Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> paramsMap = new HashMap<>();
                    paramsMap.put(RESTConstants.TOKEN, token);
                    return paramsMap;
                }
            });
        }
    }

    private void onSuccessfulLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void attemptLogin() {
        usernameView.setError(null);
        passwordView.setError(null);

        final String username = usernameView.getText().toString();
        final String password = passwordView.getText().toString();

        showProgress(true);

        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(new StringRequest(Request.Method.POST, "https://guarded-caverns-89583.herokuapp.com/user/login", new Response.Listener<String>() {
            @Override
            public void onResponse(String stringResponse) {
                try {
                    JSONObject response = new JSONObject(stringResponse);
                    if (response.getString(RESTConstants.STATUS).equals(RESTConstants.SUCCESS)) {
                        String token = response.getString(RESTConstants.TOKEN);
                        getSharedPreferences(RESTConstants.PREF, MODE_PRIVATE).edit().putString(RESTConstants.TOKEN, token).commit();
                        onSuccessfulLogin();
                    } else {
                        passwordView.setError("Wrong credentials");
                        passwordView.requestFocus();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                showProgress(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, R.string.check_internet_connection, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put(RESTConstants.USERNAME, username);
                paramsMap.put(RESTConstants.PASSWORD, password);
                return paramsMap;
            }
        });
    }

    private void attemptRegister() {
        usernameView.setError(null);
        passwordView.setError(null);

        final String username = usernameView.getText().toString();
        final String password = passwordView.getText().toString();

        showProgress(true);

        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(new StringRequest(Request.Method.POST, "https://guarded-caverns-89583.herokuapp.com/user/register", new Response.Listener<String>() {
            @Override
            public void onResponse(String stringResponse) {
                try {
                    JSONObject response = new JSONObject(stringResponse);
                    if (response.getString(RESTConstants.STATUS).equals(RESTConstants.SUCCESS)) {
                        String token = response.getString(RESTConstants.TOKEN);
                        getSharedPreferences(RESTConstants.PREF, MODE_PRIVATE).edit().putString(RESTConstants.TOKEN, token).commit();
                        onSuccessfulLogin();
                    } else {
                        switch (response.getString(RESTConstants.MESSAGE)) {
                            case BAD_PASSWORD:
                                passwordView.setError("Bad password");
                                passwordView.requestFocus();
                                break;
                            case BAD_USERNAME:
                                usernameView.setError("Bad username");
                                usernameView.requestFocus();
                                break;
                            case USERNAME_OCCUPIED:
                                usernameView.setError("Username is already occupied");
                                usernameView.requestFocus();
                                break;

                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                showProgress(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, R.string.check_internet_connection, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put(RESTConstants.USERNAME, username);
                paramsMap.put(RESTConstants.PASSWORD, password);
                return paramsMap;
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.setEnabled(!show);
        loginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


}

