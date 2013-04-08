package jplume.example;

import java.io.File;
import jplume.annotations.PathVar;
import jplume.annotations.View;
import jplume.conf.Settings;
import jplume.http.HttpFileResponse;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;

public class ExampleAction {

	public String index(){
		return "Hello world";
	}
	
	public String query(@PathVar int id, Request request) {
		request.getQuery();
		return id + "";
	}
	
	@View(pattern = "^/dynamic/([\\d]+)$")
	public String dynamic(@PathVar int id, Request request) {
		request.getQuery();
		return id + "";
	}
	
	@View(pattern = "^/dynamic/([\\d]+)/([\\w]+)$")
	public String dynamic(@PathVar int id, @PathVar String name, Request request) {
		request.getQuery();
		return id + "," + name;
	}
	
	public Response media(@PathVar(index=1) String mediaPath) {
		String mediaRoot = Settings.get("MEDIA_ROOT");
		if (mediaRoot == null) {
			return HttpResponse.notFound();
		}
		
		File mediaRootFile = new File(mediaRoot);
		File mediaFile = new File(mediaRootFile, mediaPath);
		if (!mediaFile.exists()) {
			return HttpResponse.notFound();
		}
		return new HttpFileResponse(mediaFile);
	}
}
