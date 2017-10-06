/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:40:47Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.externalConstraints;

import org.chocosolver.solver.variables.IntVar;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 3 feb. 2009
 * Time: 16:29:15
 * To change this template use File | Settings | File Templates.
 */
public final class DistGeq extends ExternalConstraint implements Serializable {

    public int D;
    public int o1;
    public int o2;
    public int q;
    public IntVar DVar = null;

    public DistGeq(int ectrID, int[] dimensions, int[] objectIdentifiers, int D_, int q_) {
        super(ectrID, dimensions, null);
        int[] oids = new int[1];
        oids[0] = objectIdentifiers[0];

        setObjectIds(oids); //Prune only the first object!
        D = D_;
        o1 = objectIdentifiers[0];
        o2 = objectIdentifiers[1];
        q = q_;
    }

    public DistGeq(int ectrID, int[] dimensions, int[] objectIdentifiers, int D_, int q_, IntVar var) {
        super(ectrID, dimensions, null);
        int[] oids = new int[1];
        oids[0] = objectIdentifiers[0];
        setObjectIds(oids); //Prune only the first object!
        D = D_;
        o1 = objectIdentifiers[0];
        o2 = objectIdentifiers[1];
        q = q_;
        DVar = var;
    }


    public String toString() {
        StringBuilder r = new StringBuilder();
        if (DVar != null) {
            r.append("Geq(D=[").append(DVar.getLB()).append(",").append(DVar.getUB())
                    .append("],q=").append(q).append(",o1=").append(o1).append(",o2=").append(o2).append(")");
        } else {
            r.append("Geq(D=").append(D).append(",q=").append(q).append(",o1=").append(o1)
                    .append(",o2=").append(o2).append(")");
        }
        return r.toString();
    }

    public boolean hasDistanceVar() {
        return (DVar != null);
    }

    public IntVar getDistanceVar() {
        return DVar;
    }


}
