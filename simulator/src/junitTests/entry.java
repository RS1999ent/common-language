package junitTests;

import java.util.List;

public class entry {

	@Override
	public String toString() {
		return "entry [path=" + path + ", metric=" + metric + "]";
	}
	public List<Integer> path;
	public Float metric;
	public Integer dst;
}
