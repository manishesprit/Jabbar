package com.jabbar.AnimationMaster.initializers;


import com.jabbar.AnimationMaster.Particle;

import java.util.Random;

public interface ParticleInitializer {

	void initParticle(Particle p, Random r);

}
