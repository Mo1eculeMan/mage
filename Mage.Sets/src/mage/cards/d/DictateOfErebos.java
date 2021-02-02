
package mage.cards.d;

import java.util.UUID;
import mage.abilities.common.DiesCreatureTriggeredAbility;
import mage.abilities.effects.common.SacrificeOpponentsEffect;
import mage.abilities.keyword.FlashAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.TargetController;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.common.FilterCreaturePermanent;

/**
 *
 * @author LevelX2
 */
public final class DictateOfErebos extends CardImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("a creature you control");

    static {
        filter.add(TargetController.YOU.getControllerPredicate());
    }

    public DictateOfErebos(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{3}{B}{B}");

        // Flash
        this.addAbility(FlashAbility.getInstance());
        // Whenever a creature you control dies, each opponent sacrifices a creature.
        this.addAbility(new DiesCreatureTriggeredAbility(new SacrificeOpponentsEffect(new FilterControlledCreaturePermanent("creature")), false, filter));
    }

    private DictateOfErebos(final DictateOfErebos card) {
        super(card);
    }

    @Override
    public DictateOfErebos copy() {
        return new DictateOfErebos(this);
    }
}
