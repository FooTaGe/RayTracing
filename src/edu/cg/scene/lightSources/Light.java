package edu.cg.scene.lightSources;

import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public abstract class Light {
	protected Vec intensity = new Vec(1, 1, 1); //white color
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Intensity: " + intensity + endl;
	}
	
	public Light initIntensity(Vec intensity) {
		this.intensity = intensity;
		return this;
	}
	
	//TODO: add some methods
	public abstract Ray rayToLight(Point point);

	public abstract Vec calcLightIntensity(Point point);

    public abstract boolean shadowedBy(Surface currSurface, Ray rayToLight);


}
