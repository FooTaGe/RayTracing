package edu.cg.scene.lightSources;

import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class DirectionalLight extends Light {
	private Vec direction = new Vec(0, -1, -1);

	public DirectionalLight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Directional Light:" + endl + super.toString() +
				"Direction: " + direction + endl;
	}

	@Override
	public DirectionalLight initIntensity(Vec intensity) {
		return (DirectionalLight)super.initIntensity(intensity);
	}


	//TODO: add some methods

	@Override
	public Ray rayToLight(Point point) {
		return new Ray(point, point.add(direction.neg()));
	}

	@Override
	public Vec calcLightIntensity(Point point) {
		return super.intensity;
	}

    @Override
    public boolean shadowedBy(Surface currSurface, Ray rayToLight) {
        if(currSurface.intersect(rayToLight) == null){
            return true;
        }
        return false;
    }


}
