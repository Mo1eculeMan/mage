
package mage.cards.s;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.effects.common.counter.AddCountersTargetEffect;
import mage.abilities.keyword.ProwessAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.events.DamageCreatureEvent;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author stravant
 */
public final class SoulScarMage extends CardImpl {

    public SoulScarMage(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{R}");

        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.WIZARD);
        this.power = new MageInt(1);
        this.toughness = new MageInt(2);

        // Prowess
        this.addAbility(new ProwessAbility());

        // If a source you control would deal noncombat damage to a creature an opponent controls, put that many -1/-1 counters on that creature instead.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new SoulScarMageDamageReplacementEffect()));
    }

    private SoulScarMage(final SoulScarMage card) {
        super(card);
    }

    @Override
    public SoulScarMage copy() {
        return new SoulScarMage(this);
    }
}

class SoulScarMageDamageReplacementEffect extends ReplacementEffectImpl {

    public SoulScarMageDamageReplacementEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = "If a source you control would deal noncombat damage to a creature an opponent controls, put that many -1/-1 counters on that creature instead.";
    }

    public SoulScarMageDamageReplacementEffect(final SoulScarMageDamageReplacementEffect effect) {
        super(effect);
    }

    @Override
    public SoulScarMageDamageReplacementEffect copy() {
        return new SoulScarMageDamageReplacementEffect(this);
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        Permanent toGetCounters = game.getPermanent(event.getTargetId());
        if (toGetCounters != null) {
            AddCountersTargetEffect addCounters = new AddCountersTargetEffect(CounterType.M1M1.createInstance(), StaticValue.get(event.getAmount()));
            addCounters.setTargetPointer(new FixedTarget(toGetCounters.getId()));
            addCounters.apply(game, source);
            return true;
        }
        return false;
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.DAMAGE_CREATURE;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        UUID sourceControllerId = game.getControllerId(event.getSourceId());
        UUID targetControllerId = game.getControllerId(event.getTargetId());
        UUID controllerId = source.getControllerId();
        boolean weControlSource = controllerId.equals(sourceControllerId);
        boolean opponentControlsTarget = game.getOpponents(sourceControllerId).contains(targetControllerId);
        boolean isNoncombatDamage = !((DamageCreatureEvent) event).isCombatDamage();
        return weControlSource
                && isNoncombatDamage
                && opponentControlsTarget;
    }
}
