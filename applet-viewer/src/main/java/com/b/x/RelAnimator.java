package com.b.x;

import java.util.Timer;
import java.util.TimerTask;

import com.b.u.AnimatorOp;

import g.algorithms.util.IterativeContext;
import g.visualization.util.Animator;

public class RelAnimator extends Animator {	
	protected AnimatorOp animatorOp;
	
	public AnimatorOp getAnimatorOp() {
		return animatorOp;
	}

	public void setAnimatorOp(AnimatorOp animatorOp) {
		this.animatorOp = animatorOp;
	}

	public RelAnimator(IterativeContext process) {
		this(process, 10L);
		//super(process);
	}

	public RelAnimator(IterativeContext process, long sleepTime) {
		super(process, sleepTime);
	}
	
	
	@Override
	public synchronized void run() {
		Timer t = new Timer();
		AnimatorTask  task = new AnimatorTask();
		t.schedule(task, 1, sleepTime);
	}
	
	public void run_sleep() {
		while (!process.done() && !stop) {

			process.step();
			if(animatorOp !=null){
				animatorOp.step();
			}

			if (stop) {
				animatorOp.stop();
				return;
			}				

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException ie) {
			}
		}
		if(animatorOp !=null){
			animatorOp.finish();
		}
	}
	
	class AnimatorTask extends TimerTask
	{
		@Override
		public void run() {
			if (!process.done() && !stop) {

				process.step();
				if(animatorOp !=null){
					animatorOp.step();
				}

//				if (stop) {
//					animatorOp.stop();
//					this.cancel();
//				}
			}
			else
			{
				if(animatorOp !=null){
					animatorOp.finish() ;				
				}
				this.cancel();
			}
		}		
	}
}
