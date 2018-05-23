package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.*;

public class Sphere extends Shape {
	private Point center;
	private double radius;
	
	public Sphere(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}
	
	public Sphere() {
		this(new Point(0, -0.5, -6), 0.5);
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Sphere:" + endl + 
				"Center: " + center + endl +
				"Radius: " + radius + endl;
	}
	
	public Sphere initCenter(Point center) {
		this.center = center;
		return this;
	}
	
	public Sphere initRadius(double radius) {
		this.radius = radius;
		return this;
	}
	
	@Override
	public Hit intersect(Ray ray) {
		Vec L = center.sub(ray.source());
		double tm = L.dot(ray.direction());
		double d2 = L.lengthSqr() - (tm * tm);
		double r2 = Math.pow(radius, 2);
		if(d2 > r2){
			return null;
		}
		double th = Math.sqrt(r2 - d2);
		double t1 = tm - th;
		double t2 = tm + th;

		if (t2 < Ops.epsilon){
            return null;
        }
        if (t1 < Ops.epsilon){
            if (t2 > Ops.infinity){
                return null;
            }
            return new Hit(t2, getNormalAtPoint(ray.add(t2)).neg());
        }

        if (t1 > Ops.infinity){
		    return null;
        }

        return new Hit(t1, getNormalAtPoint(ray.add(t1)));
	}

	private Vec getNormalAtPoint(Point p) {
		return p.sub(this.center).normalize();
	}
}
