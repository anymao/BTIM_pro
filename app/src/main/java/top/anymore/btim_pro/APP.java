package top.anymore.btim_pro;

import android.app.Application;

import com.facebook.stetho.InspectorModulesProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.facebook.stetho.rhino.JsRuntimeReplFactoryBuilder;

import okhttp3.OkHttpClient;

/**
 * Created by anymore on 17-3-26.
 */

public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        Stetho.initializeWithDefaults(this);
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(new InspectorModulesProvider() {
                    @Override
                    public Iterable<ChromeDevtoolsDomain> get() {
                        return new Stetho.DefaultInspectorModulesBuilder(APP.this).runtimeRepl(
                                new JsRuntimeReplFactoryBuilder(APP.this)
                                        // Pass to JavaScript: var foo = "bar";
                                        .addVariable("foo", "bar")
                                        .build()
                        ).finish();
                    }
                })
                .build());
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }
}
