package pe.kr.rxandroidsample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("pe.kr.rxandroidsample", appContext.getPackageName());
    }

    @Test
    public void test_assert_file() throws Exception{

        Context appContext = InstrumentationRegistry.getTargetContext();
        List<String> contents = CommonUtils.readFromfile( appContext , "names.txt");
        System.out.println(contents);
        assertThat( contents , hasItem("Elinore Moise"));
    }

    @Test
    public void test_get_githubservice() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        String owner = appContext.getString(R.string.rxjava_gist_owner_name);
        String repo = appContext.getString(R.string.rxjava_repo_name);

        List<String> logins = GithubService
                .createGitHubApi()
                .contributors(owner , repo)
                .toFlowable()
                .flatMap( items ->
                        Flowable.fromIterable(items)
                                .map(contributor -> contributor.name)
                                .toList().toFlowable()
                        )
                .subscribeOn(Schedulers.io())
                .blockingSingle();

//        Contributor expectedControbutor = new Contributor( "benjchristensen" , 0);

        //잘 가져왔는지 체크
        assertThat( logins , hasItem("benjchristensen") );
    }

}
