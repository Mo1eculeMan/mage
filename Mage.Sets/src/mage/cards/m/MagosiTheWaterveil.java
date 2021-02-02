
package mage.cards.m;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTappedAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.RemoveCountersSourceCost;
import mage.abilities.costs.common.ReturnToHandFromBattlefieldSourceCost;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.effects.common.turn.AddExtraTurnControllerEffect;
import mage.abilities.effects.common.turn.SkipNextTurnSourceEffect;
import mage.abilities.mana.BlueManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Zone;
import mage.counters.CounterType;

/**
 *
 * @author jeffwadsworth
 */
public final class MagosiTheWaterveil extends CardImpl {

    public MagosiTheWaterveil(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.LAND},"");

        // Magosi, the Waterveil enters the battlefield tapped.
        this.addAbility(new EntersBattlefieldTappedAbility());

        // {T}: Add {U}.
        this.addAbility(new BlueManaAbility());

        // {U}, {T}: Put an eon counter on Magosi, the Waterveil. Skip your next turn.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new AddCountersSourceEffect(CounterType.EON.createInstance()), new ManaCostsImpl("{U}"));
        ability.addEffect(new SkipNextTurnSourceEffect());
        ability.addCost(new TapSourceCost());
        this.addAbility(ability);

        // {T}, Remove an eon counter from Magosi, the Waterveil and return it to its owner's hand: Take an extra turn after this one.
        Ability ability2 = new SimpleActivatedAbility(Zone.BATTLEFIELD, new AddExtraTurnControllerEffect(), new TapSourceCost());
        ability2.addCost(new RemoveCountersSourceCost(CounterType.EON.createInstance()));
        ability2.addCost(new ReturnToHandFromBattlefieldSourceCost());
        this.addAbility(ability2);

    }

    private MagosiTheWaterveil(final MagosiTheWaterveil card) {
        super(card);
    }

    @Override
    public MagosiTheWaterveil copy() {
        return new MagosiTheWaterveil(this);
    }
}
