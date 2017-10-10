package pe.kr.rxandroidsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.apache.commons.lang3.StringUtils;

public class RxEventBusMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_event_bus_main);

        String title = getIntent().getStringExtra("title");

        if(!StringUtils.isEmpty(title)) {
            setTitle(title);
        }
    }
}
