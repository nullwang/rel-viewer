package com.b.x;

import com.b.u.AnimatorOp;

import g.algorithms.layout.Layout;
import g.visualization.VisualizationViewer;

public class AnimatorLayoutTransition<V,E> {
	protected RelLayoutTransition<V,E> layoutTransition;
	protected RelAnimator animator;
	protected AnimatorOp animatorOp;
	
	public AnimatorLayoutTransition(VisualizationViewer<V,E> vv, Layout<V, E> startLayout, Layout<V, E> endLayout) {
		layoutTransition = new RelLayoutTransition<V,E>(vv,startLayout,endLayout);
		animator = new RelAnimator(layoutTransition);
	}
	
	public AnimatorOp getAnimatorOp() {
		return animatorOp;
	}

	public void setAnimatorOp(AnimatorOp animatorOp) {
		animator.setAnimatorOp(animatorOp);
		this.animatorOp = animatorOp;
	}

	public void go()
	{
		animator.start();
	}
}
