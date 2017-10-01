package pe.kr.rxandroidsample;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubApi {
    @GET("/repos/{owner}/{repo}/contributors")
    Single<List<Contributor>> contributors(
            @Path("owner") String owner, @Path("repo") String repo);

}
