package com.example.helloworld;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// Import these additional packages
import android.widget.Toast;
import android.os.Looper;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {

    // Define the button and text variable
    private Button button;
    private TextView text;

    // URL of your Flask server - update with your server's IP address
    private final String SERVER_URL = "http://10.169.1.142:5000/hello";  // 10.0.2.2 points to localhost when using Android emulator

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views by ID that we have specified in the activity_main.xml file
        button = findViewById(R.id.helloButton);
        text = findViewById(R.id.helloText);

        // Set up button click listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the "Hello World" text when the button is clicked
                text.setVisibility(View.VISIBLE);
                sendHelloMessage();
            }
        });
    }

    // Method to send the message to our server
    private void sendHelloMessage() {
        // We need to create a new thread for network operations
        // (Android doesn't allow network calls on the main UI thread)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create a connection to our server
                    URL url = new URL(SERVER_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Configure the connection for a POST request
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Create a simple JSON messageS
                    String message = "{\"message\":\"Hello World\"}";

                    // Send the data
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = message.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    // Check if the server accepted our message
                    int responseCode = connection.getResponseCode();

                    // Close the connection
                    connection.disconnect();

                    // Show a success message (must be done on the UI thread)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,
                                    "Message sent to server!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    // Silently handle any errors (no error message shown)
                }
            }
        }).start();
    }
}