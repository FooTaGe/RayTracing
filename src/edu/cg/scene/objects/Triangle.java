package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class Triangle extends Shape {
	private Point p1, p2, p3;
	private transient Plain myPlain;

	public Triangle() {
		p1 = p2 = p3 = null;
	}
	
	public Triangle(Point p1, Point p2, Point p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Triangle:" + endl +
				"p1: " + p1 + endl + 
				"p2: " + p2 + endl +
				"p3: " + p3 + endl;
	}

	@Override
	public Hit intersect(Ray ray) {
		//TODO: implement this method.
		Hit planeHit = getMyPlain().intersect(ray);
		if(planeHit != null && isInside(ray, planeHit)){
            return planeHit;
        }

        return null;
	}

	private synchronized Plain getMyPlain(){
	    if (myPlain == null){
	        Vec normal = p2.sub(p1).cross(p3.sub(p1)).normalize();
	        myPlain = new Plain(normal, p1);
        }

        return myPlain;
    }

    private boolean isInside(Ray r, Hit hit){
        Vec v1 = p1.sub(r.source());
        Vec v2 = p2.sub(r.source());
        Vec v3 = p2.sub(r.source());
        Vec n1 = v2.mult(v1).mult(1 / v2.mult(v1).length());
        Vec n2 = v3.mult(v2).mult(1 / v3.mult(v2).length());
        Vec n3 = v1.mult(v2).mult(1 / v1.mult(v2).length());
        Vec pP0 = r.getHittingPoint(hit).sub(r.source());

        if (pP0.dot(n1) >= 0){
            return pP0.dot(n2) >= 0 && pP0.dot(n3) >= 0;
        }
        else{
            return pP0.dot(n2) < 0 && pP0.dot(n3) < 0;
        }

    }

    //changed
//    @Override
//    public Hit intersect(Ray ray) {
//	    //changed
//        Hit hit = getMyPlain().intersect(ray);
//        if (hit != null && this.isInside(ray, hit)) {
//            return hit;
//        }
//        return null;
//    }
}
