package com.google.ar.sceneform.samples.gltf.transform;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class MyTransformableNode extends TransformableNode {

  private final RaiseController raiseController;

  public MyTransformableNode(TransformationSystem transformationSystem,
                             TwoFingerDragGestureRecognizer twoFingerDragGestureRecognizer, Node raisedChild) {
    super(transformationSystem);
    raiseController = new RaiseController(this, twoFingerDragGestureRecognizer, raisedChild);
    addTransformationController(raiseController);
  }

}
