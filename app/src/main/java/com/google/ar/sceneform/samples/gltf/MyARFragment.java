package com.google.ar.sceneform.samples.gltf;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class MyARFragment extends ArFragment {

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        config.setPlaneFindingMode(Config.PlaneFindingMode.VERTICAL);
        return config;
    }

}
