package edu.cg.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.cg.Logger;
import edu.cg.algebra.*;
import edu.cg.scene.lightSources.Light;
import edu.cg.scene.objects.Surface;

public class Scene {
	private String name = "scene";
	private int maxRecursionLevel = 1;
	private int antiAliasingFactor = 1; //gets the values of 1, 2 and 3
	private boolean renderRefarctions = false;
	private boolean renderReflections = false;
	
	private Point camera = new Point(0, 0, 5);
	private Vec ambient = new Vec(1, 1, 1); //white
	private Vec backgroundColor = new Vec(0, 0.5, 1); //blue sky
	private List<Light> lightSources = new LinkedList<>();
	private List<Surface> surfaces = new LinkedList<>();
	
	
	//MARK: initializers
	public Scene initCamera(Point camera) {
		this.camera = camera;
		return this;
	}
	
	public Scene initAmbient(Vec ambient) {
		this.ambient = ambient;
		return this;
	}
	
	public Scene initBackgroundColor(Vec backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}
	
	public Scene addLightSource(Light lightSource) {
		lightSources.add(lightSource);
		return this;
	}
	
	public Scene addSurface(Surface surface) {
		surfaces.add(surface);
		return this;
	}
	
	public Scene initMaxRecursionLevel(int maxRecursionLevel) {
		this.maxRecursionLevel = maxRecursionLevel;
		return this;
	}
	
	public Scene initAntiAliasingFactor(int antiAliasingFactor) {
		this.antiAliasingFactor = antiAliasingFactor;
		return this;
	}
	
	public Scene initName(String name) {
		this.name = name;
		return this;
	}
	
	public Scene initRenderRefarctions(boolean renderRefarctions) {
		this.renderRefarctions = renderRefarctions;
		return this;
	}
	
	public Scene initRenderReflections(boolean renderReflections) {
		this.renderReflections = renderReflections;
		return this;
	}
	
	//MARK: getters
	public String getName() {
		return name;
	}
	
	public int getFactor() {
		return antiAliasingFactor;
	}
	
	public int getMaxRecursionLevel() {
		return maxRecursionLevel;
	}
	
	public boolean getRenderRefarctions() {
		return renderRefarctions;
	}
	
	public boolean getRenderReflections() {
		return renderReflections;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator(); 
		return "Camera: " + camera + endl +
				"Ambient: " + ambient + endl +
				"Background Color: " + backgroundColor + endl +
				"Max recursion level: " + maxRecursionLevel + endl +
				"Anti aliasing factor: " + antiAliasingFactor + endl +
				"Light sources:" + endl + lightSources + endl +
				"Surfaces:" + endl + surfaces;
	}
	
	private static class IndexTransformer {
		private final int max;
		private final int deltaX;
		private final int deltaY;
		
		IndexTransformer(int width, int height) {
			max = Math.max(width, height);
			deltaX = (max - width) / 2;
			deltaY = (max - height) / 2;
		}
		
		Point transform(int x, int y) {
			double xPos = (2*(x + deltaX) - max) / ((double)max);
			double yPos = (max - 2*(y + deltaY)) / ((double)max);
			return new Point(xPos, yPos, 0);
		}
	}
	
	private transient IndexTransformer transformaer = null;
	private transient ExecutorService executor = null;
	private transient Logger logger = null;
	
	private void initSomeFields(int imgWidth, int imgHeight, Logger logger) {
		this.logger = logger;
		//TODO: initialize your additional field here.
	}
	
	
	public BufferedImage render(int imgWidth, int imgHeight, Logger logger)
			throws InterruptedException, ExecutionException {
		
		initSomeFields(imgWidth, imgHeight, logger);
		
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		transformaer = new IndexTransformer(imgWidth, imgHeight);
		int nThreads = Runtime.getRuntime().availableProcessors();
		nThreads = nThreads < 2 ? 2 : nThreads;
		this.logger.log("Intitialize executor. Using " + nThreads + " threads to render " + name);
		executor = Executors.newFixedThreadPool(nThreads);
		
		@SuppressWarnings("unchecked")
		Future<Color>[][] futures = (Future<Color>[][])(new Future[imgHeight][imgWidth]);
		
		this.logger.log("Starting to shoot " +
			(imgHeight*imgWidth*antiAliasingFactor*antiAliasingFactor) +
			" rays over " + name);
		
		for(int y = 0; y < imgHeight; ++y)
			for(int x = 0; x < imgWidth; ++x)
				futures[y][x] = calcColor(x, y);
		
		this.logger.log("Done shooting rays.");
		this.logger.log("Wating for results...");
		
		for(int y = 0; y < imgHeight; ++y)
			for(int x = 0; x < imgWidth; ++x) {
				Color color = futures[y][x].get();
				img.setRGB(x, y, color.getRGB());
			}
		
		executor.shutdown();
		
		this.logger.log("Ray tracing of " + name + " has been completed.");
		
		executor = null;
		transformaer = null;
		this.logger = null;
		
		return img;
	}
	
	private Future<Color> calcColor(int x, int y) {
		return executor.submit(() -> {
			//TODO: change this method implementation to implement super sampling
			Point pointOnScreenPlain = transformaer.transform(x, y);
			Ray ray = new Ray(camera, pointOnScreenPlain);
			return calcColor(ray, 0).toColor();
		});
	}
	
	private Vec calcColor(Ray ray, int recusionLevel) {
		if(maxRecursionLevel <= recusionLevel){
			return new Vec(0, 0, 0);
		}
        Hit hit = findIntersection(ray);
        Vec ans;
		if (hit != null){
		    ans = calcLocalColor(ray, hit);
            Surface hitSurface = hit.getSurface();
		    if(renderRefarctions){
                //Vec refractedVec = Ops.refract(ray.direction(), hit.getNormalToSurface(), hit.getSurface().n1(hit), hit.getSurface().n2(hit));
                //Ray refractedRay = new Ray(ray.getHittingPoint(hit), refractedVec);
                //ans.add(refactedColor);
                //Todo
            }

            if(renderReflections){
                Vec reflectedVec = Ops.reflect(ray.direction(), hit.getNormalToSurface());
                Ray reflectedRay = new Ray(ray.getHittingPoint(hit), reflectedVec);
                Vec reflectedColor = calcColor(reflectedRay, ++recusionLevel).mult(hitSurface.Ks().mult(hitSurface.reflectionIntensity()));
                ans.add(reflectedColor);
            }
        }

        else {
            ans = backgroundColor;
        }

		return ans;
	}

    private Hit findIntersection(Ray ray) {
        Hit minHit = new Hit(Integer.MAX_VALUE, new Vec(0, 0,0));
	    for (Surface surface: surfaces) {
            Hit curr = surface.intersect(ray);
            if(curr != null){
                minHit = curr.compareTo(minHit) == -1 ? curr : minHit;
            }
        }
        return minHit.t() != Integer.MAX_VALUE ? minHit : null;
    }

    private Vec calcLocalColor(Ray ray, Hit hit) {
        Surface hitSurface = hit.getSurface();
        //calc ambient
	    Vec ans = hitSurface.Ka().mult(ambient);
	    //diffuse & specular
        Vec temp;
        for (Light lightSource: this.lightSources) {
            if(shadowed(ray.getHittingPoint(hit), lightSource)){
                continue;
            }
            temp = calcDiffuse(hit, lightSource, ray.getHittingPoint(hit));
            temp.add(calcSpecular(hit, lightSource, ray));
            temp.mult(lightSource.calcLightIntensity(ray.getHittingPoint(hit)));
            ans.add(temp);
        }
        return ans;
    }

    private Vec calcSpecular(Hit hit, Light lightSource, Ray ray) {
        Vec V = ray.direction().neg().normalize();
        Vec N = hit.getNormalToSurface().normalize();
        Vec L = lightSource.rayToLight(ray.getHittingPoint(hit)).direction();
        Vec R = Ops.reflect(L.neg(), N).normalize();
        double dotP = Math.max(V.dot(R), 0.0);
        return hit.getSurface().Ks().mult(Math.pow(dotP, hit.getSurface().shininess()));


    }

    private Vec calcDiffuse(Hit hit, Light lightSource, Point hitPoint) {
	    Vec normSurfaceNormal = hit.getNormalToSurface().normalize();
	    Ray rayToLight = lightSource.rayToLight(hitPoint);
	    Vec normVecToLight = rayToLight.direction().normalize();
	    double dotProduct = normSurfaceNormal.dot(normVecToLight);
	    dotProduct = Math.max(dotProduct, 0.0);
	    return hit.getSurface().Kd(hitPoint).mult(dotProduct);
    }

    private boolean shadowed(Point hittingPoint, Light lightSource) {
	    Ray rayToLight = lightSource.rayToLight(hittingPoint);
        for (Surface currSurface:this.surfaces) {
            if(currSurface.intersect(rayToLight) != null){
                return true;
            }
        }
        return false;
    }
}
