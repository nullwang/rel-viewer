package com.b.x;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import g.graph.Graph;
import g.graph.util.Context;
import g.graph.util.Pair;

public class RelParallelEdgeIndexFunction<V,E> implements RelEdgeIndexFunction<V,E>
{
    protected Map<Context<Graph<V,E>,E>, Integer> edge_index = new HashMap<Context<Graph<V,E>,E>, Integer>();
    protected Map<Context<Graph<V,E>,E>, Integer> common_edge = new HashMap<Context<Graph<V,E>,E>, Integer>();
    
    private RelParallelEdgeIndexFunction() {
    }
    
     public static <V,E> RelParallelEdgeIndexFunction<V,E> getInstance() {
        return new RelParallelEdgeIndexFunction<V,E>();
    }
    
   public int getIndex(Graph<V,E> graph, E e)
    {
    	
        Integer index = edge_index.get(Context.<Graph<V,E>,E>getInstance(graph,e));
        	//edge_index.get(e);
        if(index == null) {
        	Pair<V> endpoints = graph.getEndpoints(e);
        	V u = endpoints.getFirst();
        	V v = endpoints.getSecond();
        	if(u.equals(v)) {
        		index = getIndex(graph, e, v);
        	} else {
        		index = getIndex(graph, e, u, v);
        	}
        }
        return index.intValue();
    }

    protected int getIndex(Graph<V,E> graph, E e, V v, V u) {
    	Collection<E> commonEdgeSet = new HashSet<E>(graph.getIncidentEdges(u));
    	commonEdgeSet.retainAll(graph.getIncidentEdges(v));
    	for(Iterator<E> iterator=commonEdgeSet.iterator(); iterator.hasNext(); ) {
    		E edge = iterator.next();
    		Pair<V> ep = graph.getEndpoints(edge);
    		V first = ep.getFirst();
    		V second = ep.getSecond();
    		// remove loops
    		if(first.equals(second) == true) {
    			iterator.remove();
    		}
    		// remove edges in opposite direction
    		if(first.equals(v) == false) {
    			iterator.remove();
    		}
    	}
    	
    	putCommonEdge(graph,e,commonEdgeSet.size());
    	
    	int count=0;
    	for(E other : commonEdgeSet) {
    		if(e.equals(other) == false) {
    			edge_index.put(Context.<Graph<V,E>,E>getInstance(graph,other), count);
    			putCommonEdge(graph,other,commonEdgeSet.size());    			
    			count++;
    		}
    	}
    	edge_index.put(Context.<Graph<V,E>,E>getInstance(graph,e), count);
    	return count;
     }
    
    protected int getIndex(Graph<V,E> graph, E e, V v) {
    	Collection<E> commonEdgeSet = new HashSet<E>();
    	for(E another : graph.getIncidentEdges(v)) {
    		V u = graph.getOpposite(v, another);
    		if(u.equals(v)) {
    			commonEdgeSet.add(another);
    		}
    	}
    	putCommonEdge(graph,e,commonEdgeSet.size());
    	
    	int count=0;
    	for(E other : commonEdgeSet) {
    		if(e.equals(other) == false) {
    			edge_index.put(Context.<Graph<V,E>,E>getInstance(graph,other), count);
    			putCommonEdge(graph,other,commonEdgeSet.size());    			
    			count++;
    		}
    	}
    	edge_index.put(Context.<Graph<V,E>,E>getInstance(graph,e), count);
    	return count;
    }

    
    public void reset(Graph<V,E> graph, E e) {
    	Pair<V> endpoints = graph.getEndpoints(e);
        getIndex(graph, e, endpoints.getFirst());
        getIndex(graph, e, endpoints.getFirst(), endpoints.getSecond());
    }
    
    public void putCommonEdge(Graph graph, E e, int size)
    {
    	if(common_edge.get(Context.<Graph<V,E>,E>getInstance(graph,e)) == null)
    	{
    		common_edge.put(Context.<Graph<V,E>,E>getInstance(graph,e), size);
    	}
    }
    
    public void reset()
    {
        edge_index.clear();
        common_edge.clear();
    }

    /**
     * must be called after getIndex
     */
	public int getCommonEdgeNumber(Graph<V, E> graph, E e) {
		Integer i =  common_edge.get(Context.<Graph<V,E>,E>getInstance(graph,e));
		if( i == null)
			return 1;
		else return i.intValue();
	}
}

