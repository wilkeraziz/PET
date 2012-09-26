/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.db;

/**
 *
 * @author waziz
 */
public class ExternalInforParamsAdapter implements ExternalInfoParams {

    private final int sourceMinOrder;
    private final int sourceMaxOrder;
    private final int targetMinOrder;
    private final int targetMaxOrder;
    private final int sourceMinLength;
    private final int targetMinLength;
    private final boolean sourceNoLonger;
    private final boolean targetNoLonger;

    private ExternalInforParamsAdapter(final Builder builder) {
        this.sourceMinOrder = builder.sourceMinOrder;
        this.sourceMaxOrder = builder.sourceMaxOrder;
        this.targetMinOrder = builder.targetMinOrder;
        this.targetMaxOrder = builder.targetMaxOrder;
        this.sourceMinLength = builder.sourceMinLength;
        this.targetMinLength = builder.targetMinLength;
        this.sourceNoLonger = builder.sourceNoLonger;
        this.targetNoLonger = builder.targetNoLonger;
    }

    public static class Builder {

        private int sourceMinOrder = 1;
        private int sourceMaxOrder = 4;
        private int targetMinOrder = 1;
        private int targetMaxOrder = 4;
        private int sourceMinLength = 1;
        private int targetMinLength = 1;
        private boolean sourceNoLonger = false;
        private boolean targetNoLonger = false;

        public ExternalInforParamsAdapter build() {
            return new ExternalInforParamsAdapter(this);
        }

        public Builder sourceMinOrder(final int sourceMinOrder) {
            this.sourceMinOrder = sourceMinOrder;
            return this;
        }

        public Builder sourceMaxOrder(final int sourceMaxOrder) {
            this.sourceMaxOrder = sourceMaxOrder;
            return this;
        }

        public Builder sourceMinLength(final int sourceMinLength) {
            this.sourceMinLength = sourceMinLength;
            return this;
        }

        public Builder targetMinOrder(final int targetMinOrder) {
            this.targetMinOrder = targetMinOrder;
            return this;
        }

        public Builder targetMaxOrder(final int targetMaxOrder) {
            this.targetMaxOrder = targetMaxOrder;
            return this;
        }

        public Builder targetMinLength(final int targetMinLength) {
            this.targetMinLength = targetMinLength;
            return this;
        }

        public Builder sourceNoLonger(final boolean sourceNoLonger) {
            this.sourceNoLonger = sourceNoLonger;
            return this;
        }

        public Builder targetNoLonger(final boolean targetNoLonger) {
            this.targetNoLonger = targetNoLonger;
            return this;
        }
    }

    public int sourceMinOrder() {
        return sourceMinOrder;
    }

    public int sourceMaxOrder() {
        return sourceMaxOrder;
    }

    public int targetMinOrder() {
        return targetMinOrder;
    }

    public int targetMaxOrder() {
        return targetMaxOrder;
    }

    public int sourceMinLength() {
        return sourceMinLength;
    }

    public int targetMinLengt() {
        return targetMinLength;
    }

    public boolean sourceNoLonger() {
        return sourceNoLonger;
    }

    public boolean targetNoLonger() {
        return targetNoLonger;
    }
}
