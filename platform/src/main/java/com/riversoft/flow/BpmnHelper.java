/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.flow.key.NodeType;

/**
 * @author woden
 * 
 */
@ScriptSupport("bpmn")
public class BpmnHelper {

	/**
	 * 配置节点
	 * 
	 * @author woden
	 * 
	 */
	public static class Node {

		private String id;// 控件ID

		private NodeType nodeType = NodeType.DEFAULT;// 类型
		private String name;// 展示名

		private FlowElement element;// 实体

		/**
		 * 实例化
		 * 
		 * @param element
		 */
		private Node(FlowElement element) {
			this.element = element;

			for (NodeType type : NodeType.values()) {
				if (element.getClass() == type.getType()) {
					this.nodeType = type;
					break;
				}
			}

			if (StringUtils.isNotEmpty(element.getName())) {
				this.name = element.getName();
			} else {
				this.name = "";
			}

			this.id = element.getId();
		}

		/**
		 * 外部连线
		 * 
		 * @return
		 */
		public List<SequenceFlow> getSequenceFlows() {
			// 线
			if (element instanceof FlowNode) {
				FlowNode floeNode = (FlowNode) element;
				return floeNode.getOutgoingFlows();
			}

			return new ArrayList<>();
		}

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the nodeType
		 */
		public NodeType getNodeType() {
			return nodeType;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the element
		 */
		public FlowElement getElement() {
			return element;
		}
	}

	/**
	 * 坐标信息
	 * 
	 * @author woden
	 * 
	 */
	public static class Coordinate {
		int minX;
		int maxX;
		int minY;
		int maxY;

		public Coordinate(int maxX, int maxY, int minX, int minY) {
			this.maxX = maxX;
			this.minX = minX;
			this.maxY = maxY;
			this.minY = minY;
		}

		/**
		 * @return the minX
		 */
		public int getMinX() {
			return minX;
		}

		/**
		 * @return the maxX
		 */
		public int getMaxX() {
			return maxX;
		}

		/**
		 * @return the minY
		 */
		public int getMinY() {
			return minY;
		}

		/**
		 * @return the maxY
		 */
		public int getMaxY() {
			return maxY;
		}
	}

	protected static List<FlowNode> gatherAllFlowNodes(FlowElementsContainer flowElementsContainer) {
		List<FlowNode> flowNodes = new ArrayList<FlowNode>();
		for (FlowElement flowElement : flowElementsContainer.getFlowElements()) {
			if (flowElement instanceof FlowNode) {
				flowNodes.add((FlowNode) flowElement);
			}
			if (flowElement instanceof FlowElementsContainer) {
				flowNodes.addAll(gatherAllFlowNodes((FlowElementsContainer) flowElement));
			}
		}
		return flowNodes;
	}

	protected static List<FlowNode> gatherAllFlowNodes(BpmnModel bpmnModel) {
		List<FlowNode> flowNodes = new ArrayList<FlowNode>();
		for (Process process : bpmnModel.getProcesses()) {
			flowNodes.addAll(gatherAllFlowNodes(process));
		}
		return flowNodes;
	}

	/**
	 * 获取节点信息
	 * 
	 * @param bpmnModel
	 * @param id
	 * @return
	 */
	public static Node getNode(BpmnModel bpmnModel, String id) {
		FlowElement element = bpmnModel.getFlowElement(id);
		if (element == null) {
			return null;
		}
		return new Node(element);
	}

	/**
	 * 根据节点类型获取节点
	 * 
	 * @param bpmnModel
	 * @param type
	 * @return
	 */
	public static List<Node> getNodes(BpmnModel bpmnModel, NodeType type) {
		List<? extends FlowElement> list = bpmnModel.getMainProcess().findFlowElementsOfType(type.getType());
		List<Node> nodes = new ArrayList<>();
		if (list != null) {
			for (FlowElement element : list) {
				nodes.add(new Node(element));
			}
		}
		return nodes;
	}

	/**
	 * 根据节点类型获取节点
	 * 
	 * @param bpmnModel
	 * @param type
	 * @return
	 */
	public static Node getNode(BpmnModel bpmnModel, NodeType type) {
		List<Node> list = getNodes(bpmnModel, type);
		if (list != null) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取外部连线
	 * 
	 * @param bpmnModel
	 * @param id
	 * @return
	 */
	public static List<SequenceFlow> getOuterSequenceFlows(BpmnModel bpmnModel, String id) {
		FlowElement element = bpmnModel.getFlowElement(id);
		if (element instanceof FlowNode) {
			FlowNode flowNode = (FlowNode) element;
			return flowNode.getOutgoingFlows();
		}

		return Collections.<SequenceFlow> emptyList();
	}

	/**
	 * 获取宏观坐标
	 * 
	 * @param bpmnModel
	 * @return
	 */
	public static Coordinate getCoordinateInfo(BpmnModel bpmnModel) {

		// We need to calculate maximum values to know how big the image will be
		// in its entirety
		double minX = Double.MAX_VALUE;
		double maxX = 0;
		double minY = Double.MAX_VALUE;
		double maxY = 0;

		for (Pool pool : bpmnModel.getPools()) {
			GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
			minX = graphicInfo.getX();
			maxX = graphicInfo.getX() + graphicInfo.getWidth();
			minY = graphicInfo.getY();
			maxY = graphicInfo.getY() + graphicInfo.getHeight();
		}

		List<FlowNode> flowNodes = gatherAllFlowNodes(bpmnModel);
		for (FlowNode flowNode : flowNodes) {

			GraphicInfo flowNodeGraphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());

			// width
			if (flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth() > maxX) {
				maxX = flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth();
			}
			if (flowNodeGraphicInfo.getX() < minX) {
				minX = flowNodeGraphicInfo.getX();
			}
			// height
			if (flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight() > maxY) {
				maxY = flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight();
			}
			if (flowNodeGraphicInfo.getY() < minY) {
				minY = flowNodeGraphicInfo.getY();
			}

			for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
				List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
				for (GraphicInfo graphicInfo : graphicInfoList) {
					// width
					if (graphicInfo.getX() > maxX) {
						maxX = graphicInfo.getX();
					}
					if (graphicInfo.getX() < minX) {
						minX = graphicInfo.getX();
					}
					// height
					if (graphicInfo.getY() > maxY) {
						maxY = graphicInfo.getY();
					}
					if (graphicInfo.getY() < minY) {
						minY = graphicInfo.getY();
					}
				}
			}
		}

		int nrOfLanes = 0;
		for (Process process : bpmnModel.getProcesses()) {
			for (Lane l : process.getLanes()) {

				nrOfLanes++;

				GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(l.getId());
				// // width
				if (graphicInfo.getX() + graphicInfo.getWidth() > maxX) {
					maxX = graphicInfo.getX() + graphicInfo.getWidth();
				}
				if (graphicInfo.getX() < minX) {
					minX = graphicInfo.getX();
				}
				// height
				if (graphicInfo.getY() + graphicInfo.getHeight() > maxY) {
					maxY = graphicInfo.getY() + graphicInfo.getHeight();
				}
				if (graphicInfo.getY() < minY) {
					minY = graphicInfo.getY();
				}
			}
		}

		// Special case, see http://jira.codehaus.org/browse/ACT-1431
		if (flowNodes.size() == 0 && bpmnModel.getPools().size() == 0 && nrOfLanes == 0) {
			// Nothing to show
			minX = 0;
			minY = 0;
		}

		return new Coordinate((int) maxX + 10, (int) maxY + 10, (int) minX, (int) minY);
	}
}
