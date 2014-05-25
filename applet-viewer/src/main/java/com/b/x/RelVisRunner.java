package com.b.x;

import java.util.Timer;
import java.util.TimerTask;

import g.algorithms.layout.util.VisRunner;
import g.algorithms.util.IterativeContext;

public class RelVisRunner extends VisRunner {

	public RelVisRunner(IterativeContext process) {
		super(process);
	}

	public void run() {
		Timer t = new Timer();
		AnimatorTask task = new AnimatorTask();
		t.schedule(task, 1, this.sleepTime);
	}

	class AnimatorTask extends TimerTask {
		@Override
		public void run() {
			running = true;
			if (!process.done() && !stop) {
					synchronized (pauseObject) {
						while (manualSuspend && !stop) {
							try {
								pauseObject.wait();
							} catch (InterruptedException e) {
								// ignore
							}
						}
					}
					process.step();

//					if (stop)
//						return;
				}
			else{
				running = false;
				this.cancel();
			}
		}
	}

}
