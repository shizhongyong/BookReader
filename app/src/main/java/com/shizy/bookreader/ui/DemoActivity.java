package com.shizy.bookreader.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.shizy.bookreader.R;
import com.shizy.bookreader.util.RxJavaUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class DemoActivity extends AppCompatActivity {

	private static final String TAG = DemoActivity.class.getSimpleName();

	private static final String URL = "http://www.biquge.com.tw/modules/article/soshu.php?searchkey=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);


		Observable.create(new ObservableOnSubscribe<Void>() {
			@Override
			public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
				test();
				emitter.onComplete();
			}
		})
				.compose(RxJavaUtil.<Void>mainSchedulers())
				.subscribe();

	}

	private void test() {
		try {
			Document doc = Jsoup.connect(URL + URLEncoder.encode("重生", "UTF-8")).get();
			Elements elements = doc.getAllElements();
			for (Element element : elements) {
				Log.d(TAG, "Element.tag: " + element.tag());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
