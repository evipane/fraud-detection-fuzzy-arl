package org.processmining.framework.models.bpel.visit;

import org.processmining.framework.models.bpel.BPELAssign;
import org.processmining.framework.models.bpel.BPELEmpty;
import org.processmining.framework.models.bpel.BPELFlow;
import org.processmining.framework.models.bpel.BPELInvoke;
import org.processmining.framework.models.bpel.BPELPick;
import org.processmining.framework.models.bpel.BPELProcess;
import org.processmining.framework.models.bpel.BPELReceive;
import org.processmining.framework.models.bpel.BPELReply;
import org.processmining.framework.models.bpel.BPELScope;
import org.processmining.framework.models.bpel.BPELSequence;
import org.processmining.framework.models.bpel.BPELSwitch;
import org.processmining.framework.models.bpel.BPELWait;
import org.processmining.framework.models.bpel.BPELWhile;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public abstract class BPELVisitor {
	public BPELVisitor() {
	}

	public void visit(Object o) {
		if (o instanceof BPELProcess)
			visitProcess((BPELProcess) o);
		else if (o instanceof BPELAssign)
			visitAssign((BPELAssign) o);
		else if (o instanceof BPELEmpty)
			visitEmpty((BPELEmpty) o);
		else if (o instanceof BPELFlow)
			visitFlow((BPELFlow) o);
		else if (o instanceof BPELInvoke)
			visitInvoke((BPELInvoke) o);
		else if (o instanceof BPELPick)
			visitPick((BPELPick) o);
		else if (o instanceof BPELReceive)
			visitReceive((BPELReceive) o);
		else if (o instanceof BPELReply)
			visitReply((BPELReply) o);
		else if (o instanceof BPELScope)
			visitScope((BPELScope) o);
		else if (o instanceof BPELSequence)
			visitSequence((BPELSequence) o);
		else if (o instanceof BPELSwitch)
			visitSwitch((BPELSwitch) o);
		else if (o instanceof BPELWait)
			visitWait((BPELWait) o);
		else if (o instanceof BPELWhile)
			visitWhile((BPELWhile) o);
	}

	public abstract void visitProcess(BPELProcess process);

	public abstract void visitInvoke(BPELInvoke invoke);

	public abstract void visitSequence(BPELSequence sequence);

	public abstract void visitReceive(BPELReceive receive);

	public abstract void visitEmpty(BPELEmpty empty);

	public abstract void visitReply(BPELReply reply);

	public abstract void visitScope(BPELScope scope);

	public abstract void visitWhile(BPELWhile while1);

	public abstract void visitSwitch(BPELSwitch switch1);

	public abstract void visitFlow(BPELFlow flow);

	public abstract void visitPick(BPELPick pick);

	public abstract void visitAssign(BPELAssign assign);

	public abstract void visitWait(BPELWait wait);
}
