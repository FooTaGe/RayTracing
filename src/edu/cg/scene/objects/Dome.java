package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class Dome extends Shape {
	private Sphere sphere;
	private Plain plain;
	
	public Dome() {
		sphere = new Sphere().initCenter(new Point(0, -0.5, -6));
		plain = new Plain(new Vec(-1, 0, -1), new Point(0, -0.5, -6));
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Dome:" + endl + 
				sphere + plain + endl;
	}


	@Override
	public Hit intersect(Ray ray) {
		Hit hit = this.sphere.intersect(ray);
		if (hit == null) {
			return null;
		}
        Point hittingPoint = ray.getHittingPoint(hit);
        if (plain.normal().dot(hittingPoint.toVec()) + plain.getD() > 0){
            return hit;
        }
        hit = this.plain.intersect(ray);
        if (hit == null) {
            return null;
        }
        hittingPoint = ray.getHittingPoint(hit);
        if (hittingPoint.distSqr(this.sphere.getCenter()) - Math.pow(sphere.getRadius(), 2) < 0.0) {
            return hit;
        }
        return null;
	}


}
