
package mage.cards.l;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.AllyEntersBattlefieldTriggeredAbility;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.continuous.GainAbilityAllEffect;
import mage.abilities.keyword.LifelinkAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.CardIdPredicate;

/**
 *
 * @author fireshoes
 */
public final class LanternScout extends CardImpl {

    public LanternScout(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{2}{W}");
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.SCOUT);
        this.subtype.add(SubType.ALLY);
        this.power = new MageInt(3);
        this.toughness = new MageInt(2);

        FilterPermanent filter = new FilterPermanent("{this} or another Ally");
        filter.add(Predicates.or(
                new CardIdPredicate(this.getId()),
                SubType.ALLY.getPredicate()));

        // <i>Rally</i> &mdash; Whenever Lantern Scout or another Ally enters the battlefield under your control, creatures you control gain lifelink until end of turn.
        Effect effect = new GainAbilityAllEffect(LifelinkAbility.getInstance(), Duration.EndOfTurn, new FilterControlledCreaturePermanent());
        effect.setText("creatures you control gain lifelink until end of turn");
        Ability ability = new AllyEntersBattlefieldTriggeredAbility(
                effect, false);
        this.addAbility(ability);
    }

    private LanternScout(final LanternScout card) {
        super(card);
    }

    @Override
    public LanternScout copy() {
        return new LanternScout(this);
    }
}
