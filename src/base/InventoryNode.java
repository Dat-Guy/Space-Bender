package base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class InventoryNode {

    public ArrayList<InventoryNode> connected;
    private HashMap<GameData.materials, Double> materials;
    private HashMap<GameData.components, Long> components;

    public InventoryNode() {
        connected = new ArrayList<>();
        materials = new HashMap<>();
        components = new HashMap<>();
    }

    /**
     * Adds materials to this node
     * @param m The material to add
     * @param amount The amount to add
     */
    public void addMaterials(GameData.materials m, double amount) {
        materials.put(m, amount + materials.getOrDefault(m, 0.0));
    }

    /**
     * Consumes materials from all connected pools; returns a nonzero amount if unable to fully fulfill request
     * representing the missing quantity.
     * @param m Material to consume
     * @param amount Amount of material to consume
     * @param allowIncomplete Deduct materials even if there is a deficit.
     */
    public double consumeMaterials(GameData.materials m, double amount, boolean allowIncomplete) {
        return consumeMaterials(m, amount, allowIncomplete, true);
    }

    /**
     * This is the internal function which leverages recursive tomfoolery in order to evaluate connected inventories
     * without mountains of code!
     * @param m Material to consume
     * @param amount Amount of material to consume
     * @param allowIncomplete Allows partial consumption of materials (materials are deducted even if order cannot be
     *                        fulfilled).
     * @param recursive If the inventory should query connected inventories for resources.
     * @return Returns the amount of materials required if the inventory is in deficit.
     */
    private double consumeMaterials(GameData.materials m, double amount, boolean allowIncomplete, boolean recursive) {
        if (amount <= 0.0) {
            return 0.0; //Nothing needs to be consumed
        } else {
            if (materials.containsKey(m)) {
                if (amount - materials.get(m) <= 0) {
                    //Everything was able to be withdrawn from the singular inventory
                    materials.put(m, materials.get(m) - amount);
                    return 0.0;
                } else {
                    if (allowIncomplete) {
                        // We get to do this the 'lazy' way;
                        // deduct materials until either the required amount was consumed, or until
                        // all connected inventories are emptied.

                        // Begin by emptying our own inventory
                        amount -= Objects.requireNonNull(materials.put(m, 0.0));

                        // If this is a call for recursion, begin deducting from all connected inventories
                        if (recursive) {
                            for (InventoryNode i : connected) {
                                amount -= i.consumeMaterials(m, amount, true, false);
                                // We could check the amount remaining, which would add i.size() computations to the
                                // loop worst-case scenario, and if not every inventory is run through it saves cycles
                                // exactly half the time, so no real performance gain + extra code => go for smaller
                                // program with less potential to error and crash due to bad access.
                            }
                        }

                        // Return the remaining amount (0.0 if there's none)
                        return amount;
                    } else {
                        // First, see if we're being called for recursion
                        if (recursive) {

                            // Sum up ALL materials of the specified type for ALL connected inventories in the list
                            // (note that if a deep tree exists, only the first layer is checked.
                            // If there's enough, then continue with deduction of materials, otherwise return the
                            // deficit.
                            double sum = queryMaterials(m, false);

                            if (sum >= amount) {
                                amount -= sum;
                                for (InventoryNode i : connected) {
                                    amount -= i.consumeMaterials(m, amount, false, false);
                                }
                                return 0.0;
                            } else {
                                return amount - sum;
                            }
                        } else {
                            // If not called for recursion, assume it was for the collapse of the result of a
                            // non-negative sum.
                            return Objects.requireNonNull(materials.put(m, 0.0));
                        }
                    }

                }
            } else {
                // Let's be lazy and substitute in zero, then call again. We lose a couple computations once per object
                // worst-case scenario.
                materials.put(m, 0.0);
                return consumeMaterials(m, amount, allowIncomplete, recursive);
            }
        }
    }

    /**
     * Gets the amount of a particular material for a particular inventory system
     * @param m The material to query
     * @param targetedExclusive If only the targeted inventory should be queried
     * @return The amount residing in the inventory(s)
     */
    public double queryMaterials(GameData.materials m, boolean targetedExclusive) {
        double sum = queryMaterialsInternal(m);
        if (!targetedExclusive) {
            for (InventoryNode i : connected) {
                sum += i.queryMaterialsInternal(m);
            }
        }
        return sum;
    }

    /**
     * Gets the amount of a particular material for a particular inventory
     */
    private double queryMaterialsInternal(GameData.materials m) {
        materials.putIfAbsent(m, 0.0);
        return materials.get(m);
    }

    /**
     * Adds components to this node
     * @param c The component to add
     * @param amount The amount to add
     */
    public void addComponents(GameData.components c, long amount) {
        components.put(c, amount + components.getOrDefault(c, (long) 0));
    }

    /**
     * Consumes components from all connected pools; returns a nonzero amount if unable to fully fulfill request
     * representing the missing quantity.
     * @param c Component to consume
     * @param amount Amount of component to consume
     * @param allowIncomplete Deduct components even if there is a deficit.
     */
    public double consumeComponents(GameData.components c, long amount, boolean allowIncomplete) {
        return consumeComponents(c, amount, allowIncomplete, true);
    }

    /**
     * This is the internal function which leverages recursive tomfoolery in order to evaluate connected inventories
     * without mountains of code!
     * @param c Component to consume
     * @param amount Amount of component to consume
     * @param allowIncomplete Allows partial consumption of materials (materials are deducted even if order cannot be
     *                        fulfilled).
     * @param recursive If the inventory should query connected inventories for resources.
     * @return Returns the amount of materials required if the inventory is in deficit.
     */
    private long consumeComponents(GameData.components c, long amount, boolean allowIncomplete, boolean recursive) {
        if (amount <= 0) {
            return 0; //Nothing needs to be consumed
        } else {
            if (components.containsKey(c)) {
                if (amount - components.get(c) <= 0) {
                    //Everything was able to be withdrawn from the singular inventory
                    components.put(c, components.get(c) - amount);
                    return 0;
                } else {
                    if (allowIncomplete) {
                        // We get to do this the 'lazy' way;
                        // deduct components until either the required amount was consumed, or until
                        // all connected inventories are emptied.

                        // Begin by emptying our own inventory
                        amount -= Objects.requireNonNull(components.put(c, (long) 0));

                        // If this is a call for recursion, begin deducting from all connected inventories
                        if (recursive) {
                            for (InventoryNode i : connected) {
                                amount -= i.consumeComponents(c, amount, true, false);
                                // We could check the amount remaining, which would add i.size() computations to the
                                // loop worst-case scenario, and if not every inventory is run through it saves cycles
                                // exactly half the time, so no real performance gain + extra code => go for smaller
                                // program with less potential to error and crash due to bad access.
                            }
                        }

                        // Return the remaining amount (0.0 if there's none)
                        return amount;
                    } else {
                        // First, see if we're being called for recursion
                        if (recursive) {

                            // Sum up ALL components of the specified type for ALL connected inventories in the list
                            // (note that if a deep tree exists, only the first layer is checked.
                            // If there's enough, then continue with deduction of components, otherwise return the
                            // deficit.
                            long sum = queryComponents(c, false);

                            if (sum >= amount) {
                                amount -= sum;
                                for (InventoryNode i : connected) {
                                    amount -= i.consumeComponents(c, amount, false, false);
                                }
                                return 0;
                            } else {
                                return amount - sum;
                            }
                        } else {
                            // If not called for recursion, assume it was for the collapse of the result of a
                            // non-negative sum.
                            return Objects.requireNonNull(components.put(c, (long) 0));
                        }
                    }

                }
            } else {
                // Let's be lazy and substitute in zero, then call again. We lose a couple computations once per object
                // worst-case scenario.
                components.put(c, (long) 0);
                return consumeComponents(c, amount, allowIncomplete, recursive);
            }
        }
    }

    /**
     * Gets the amount of a particular material for a particular inventory system
     * @param c The component to query
     * @param targetedExclusive If only the targeted inventory should be queried
     * @return The amount residing in the inventory(s)
     */
    public long queryComponents(GameData.components c, boolean targetedExclusive) {
        long sum = queryComponentsInternal(c);
        if (!targetedExclusive) {
            for (InventoryNode i : connected) {
                sum += i.queryComponentsInternal(c);
            }
        }
        return sum;
    }

    /**
     * Gets the amount of a particular components for a particular inventory
     */
    private long queryComponentsInternal(GameData.components c) {
        components.putIfAbsent(c, (long) 0);
        return components.get(c);
    }

}
