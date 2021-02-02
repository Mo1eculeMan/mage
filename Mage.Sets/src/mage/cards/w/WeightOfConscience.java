package mage.cards.w;

import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.common.TapTargetCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.AttachEffect;
import mage.abilities.effects.common.combat.CantAttackAttachedEffect;
import mage.abilities.keyword.EnchantAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.predicate.Predicate;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.permanent.SharesCreatureTypePredicate;
import mage.filter.predicate.permanent.TappedPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetPermanent;
import mage.target.common.TargetControlledCreaturePermanent;
import mage.target.common.TargetCreaturePermanent;

import java.util.*;

/**
 * @author emerald000
 */
public final class WeightOfConscience extends CardImpl {

    public WeightOfConscience(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{W}");
        this.subtype.add(SubType.AURA);

        // Enchant creature
        TargetPermanent auraTarget = new TargetCreaturePermanent();
        this.getSpellAbility().addTarget(auraTarget);
        this.getSpellAbility().addEffect(new AttachEffect(Outcome.Detriment));
        Ability ability = new EnchantAbility(auraTarget.getTargetName());
        this.addAbility(ability);

        // Enchanted creature can't attack.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new CantAttackAttachedEffect(AttachmentType.AURA)));

        // Tap two untapped creatures you control that share a creature type: Exile enchanted creature.
        this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new WeightOfConscienceEffect(), new TapTargetCost(new WeightOfConscienceTarget())));
    }

    private WeightOfConscience(final WeightOfConscience card) {
        super(card);
    }

    @Override
    public WeightOfConscience copy() {
        return new WeightOfConscience(this);
    }
}

class WeightOfConscienceEffect extends OneShotEffect {

    WeightOfConscienceEffect() {
        super(Outcome.Exile);
        staticText = "Exile enchanted creature";
    }

    WeightOfConscienceEffect(final WeightOfConscienceEffect effect) {
        super(effect);
    }

    @Override
    public WeightOfConscienceEffect copy() {
        return new WeightOfConscienceEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        // In the case that the enchantment is blinked
        Permanent enchantment = (Permanent) game.getLastKnownInformation(source.getSourceId(), Zone.BATTLEFIELD);
        if (enchantment == null) {
            // It was not blinked, use the standard method
            enchantment = game.getPermanentOrLKIBattlefield(source.getSourceId());
        }
        if (controller != null
                && enchantment != null
                && enchantment.getAttachedTo() != null) {
            Permanent creature = game.getPermanent(enchantment.getAttachedTo());
            if (creature != null) {
                controller.moveCardsToExile(creature, source, game, true, null, "");
            }
        }
        return false;
    }
}

class WeightOfConscienceTarget extends TargetControlledCreaturePermanent {

    private static final FilterControlledCreaturePermanent filterUntapped = new FilterControlledCreaturePermanent("untapped creatures you control that share a creature type");

    static {
        filterUntapped.add(Predicates.not(TappedPredicate.instance));
        filterUntapped.add(WeightOfConsciencePredicate.instance);
    }

    WeightOfConscienceTarget() {
        super(2, 2, filterUntapped, true);
    }

    private WeightOfConscienceTarget(final WeightOfConscienceTarget target) {
        super(target);
    }

    @Override
    public Set<UUID> possibleTargets(UUID sourceId, UUID sourceControllerId, Game game) {
        Player player = game.getPlayer(sourceControllerId);
        Set<UUID> possibleTargets = new HashSet<>(0);
        if (player == null) {
            return possibleTargets;
        }
        // Choosing first target
        if (this.getTargets().isEmpty()) {
            List<Permanent> permanentList = game.getBattlefield().getActivePermanents(filterUntapped, sourceControllerId, sourceId, game);
            if (permanentList.size() < 2) {
                return possibleTargets;
            }
            for (Permanent permanent : permanentList) {
                if (permanent.isAllCreatureTypes(game)) {
                    possibleTargets.add(permanent.getId());
                    continue;
                }
                FilterPermanent filter = filterUntapped.copy();
                filter.add(new SharesCreatureTypePredicate(permanent));
                if (game.getBattlefield().count(filter, sourceId, sourceControllerId, game) > 1) {
                    possibleTargets.add(permanent.getId());
                }
            }
        } // Choosing second target
        else {
            Permanent firstTargetCreature = game.getPermanent(this.getFirstTarget());
            if (firstTargetCreature == null) {
                return possibleTargets;
            }
            FilterPermanent filter = filterUntapped.copy();
            filter.add(new SharesCreatureTypePredicate(firstTargetCreature));
            for (Permanent permanent : game.getBattlefield().getActivePermanents(filterUntapped, sourceControllerId, sourceId, game)) {
                if (permanent != null) {
                    possibleTargets.add(permanent.getId());
                }
            }
        }
        return possibleTargets;
    }

    @Override
    public boolean canChoose(UUID sourceId, UUID sourceControllerId, Game game) {
        for (Permanent permanent1 : game.getBattlefield().getActivePermanents(filterUntapped, sourceControllerId, sourceId, game)) {
            for (Permanent permanent2 : game.getBattlefield().getActivePermanents(filterUntapped, sourceControllerId, sourceId, game)) {
                if (!Objects.equals(permanent1, permanent2) && permanent1.shareCreatureTypes(game, permanent2)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canTarget(UUID id, Ability source, Game game) {
        if (!super.canTarget(id, game)) {
            return false;
        }
        Permanent targetPermanent = game.getPermanent(id);
        if (targetPermanent == null) {
            return false;
        }
        if (this.getTargets().isEmpty()) {
            List<Permanent> permanentList = game.getBattlefield().getActivePermanents(filterUntapped, source.getControllerId(), source.getSourceId(), game);
            if (permanentList.size() < 2) {
                return false;
            }
            for (Permanent permanent : permanentList) {
                if (permanent.isAllCreatureTypes(game)) {
                    return true;
                }
                FilterPermanent filter = filterUntapped.copy();
                filter.add(new SharesCreatureTypePredicate(permanent));
                if (game.getBattlefield().count(filter, source.getSourceId(), source.getControllerId(), game) > 1) {
                    return true;
                }
            }
        } else {
            Permanent firstTarget = game.getPermanent(this.getTargets().get(0));
            return firstTarget != null && firstTarget.shareCreatureTypes(game, targetPermanent);
        }
        return false;
    }

    @Override
    public WeightOfConscienceTarget copy() {
        return new WeightOfConscienceTarget(this);
    }
}

enum WeightOfConsciencePredicate implements Predicate<MageObject> {
    instance;

    @Override
    public boolean apply(MageObject input, Game game) {
        return input.isAllCreatureTypes(game)
                || input
                .getSubtype(game)
                .stream()
                .map(SubType::getSubTypeSet)
                .anyMatch(SubTypeSet.CreatureType::equals);
    }
}
