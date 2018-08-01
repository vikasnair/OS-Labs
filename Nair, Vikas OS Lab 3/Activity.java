// representing an activity, which has a 'parent' task

public class Activity {
	Task t;
	State s;
	int delay, resource, claim, request, release;

	Activity(Task t, int delay, int resource) {
		this.t = t;
		this.s = s;
		this.delay = delay;
		this.resource = resource;
	}

	@Override
	public String toString() {
		return "{ task " + t + " }, state: " + s + ", delay: " + delay + ", resource: " + resource + ", request: " + request + ", release: " + release;
	}
}