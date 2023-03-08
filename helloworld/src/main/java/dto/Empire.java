package dto;

import java.util.List;

public class Empire {

    private int countdown;

    private List<BountyHunter> bounty_hunters;

    public Empire() {

    }

    public Empire(int countdown, List<BountyHunter> bounty_hunters) {
        this.countdown = countdown;
        this.bounty_hunters = bounty_hunters;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public List<BountyHunter> getBounty_hunters() {
        return bounty_hunters;
    }

    public void setBounty_hunters(List<BountyHunter> bounty_hunters) {
        this.bounty_hunters = bounty_hunters;
    }
}
