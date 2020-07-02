/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.blockly.model;

import android.content.Context;
import android.util.Log;

import com.google.blockly.android.control.BlocklyController;
import com.google.blockly.android.control.ConnectionManager;
import com.google.blockly.android.control.NameManager;
import com.google.blockly.android.control.ProcedureManager;
import com.google.blockly.android.control.WorkspaceStats;
import com.google.blockly.utils.BlockLoadingException;
import com.google.blockly.utils.BlocklyXmlHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

/**
 * The root class for the Blockly model.  Keeps track of all the global state used in the workspace.
 */
public class Workspace {
    private static final String TAG = "Workspace";

    private final Context mContext;
    private final BlocklyController mController;
    private BlockFactory mBlockFactory;
    private String mId;

    private final ArrayList<Block> mRootBlocks = new ArrayList<>();
    private final ProcedureManager mProcedureManager;
    private final NameManager mVariableNameManager = new NameManager.VariableNameManager();
    private final ConnectionManager mConnectionManager = new ConnectionManager();
    private final WorkspaceStats mStats;

    private BlocklyCategory mFlyoutCategory;
    private BlocklyCategory mTrashCategory = new BlocklyCategory();


    // @HTS
    public ConnectionManager.YSortedList getConnections_List(int i)
    {
        return mConnectionManager.getConnections(i);
    }
    // @HTS
    public ConnectionManager.YSortedList getBlocks_inWorkspace()
    {
        return mConnectionManager.getmNextConnections();
    }
    // @HTS
    public ConnectionManager.YSortedList getBlocks_inWorkspace_inputCon()
    {
        return mConnectionManager.getmInputConnections();
    }


//    public static BlocklyCategory mDoingStackCategory = new BlocklyCategory();

    /**
     * Create a workspace.
     *
     * @param context The context this workspace is associated with.
     * @param controller The controller for this Workspace.
     * @param factory The factory used to build blocks in this workspace.
     */
    public Workspace(Context context, BlocklyController controller, BlockFactory factory) {
        if (controller == null) {
            throw new IllegalArgumentException("BlocklyController may not be null.");
        }

        mContext = context;
        mController = controller;
        mBlockFactory = factory;
        mId = UUID.randomUUID().toString();

        mProcedureManager = new ProcedureManager(controller, this);
        mStats = new WorkspaceStats(mVariableNameManager, mProcedureManager, mConnectionManager);
    }

    /**
     * @return The string identifier of this workspace. Used by {@link BlocklyEvent events}.
     */
    public String getId() {
        return mId;
    }

    /**
     * Adds a new block to the workspace as a root block.
     *
     * @param block The block to add to the root of the workspace.
     * @param isNewBlock Set when the block is new to the workspace (compared to moving it from some
     *                   previous connection).
 *     @throws IllegalArgumentException If the block or its children are references to undefined
     *                                  procedures.
     */
    public void addRootBlock(Block block, boolean isNewBlock) {
        if (block == null) {
            throw new IllegalArgumentException("Cannot add a null block as a root block");
        }
        if (block.getPreviousBlock() != null) {
            throw new IllegalArgumentException("Root blocks may not have a previous block");
        }
        if (mRootBlocks.contains(block)) {
            throw new IllegalArgumentException("Block is already a root block.");
        }
        mRootBlocks.add(block);
        if (isNewBlock) {
            block.setEventWorkspaceId(getId());
            try {
                mStats.collectStats(block, true);
            } catch (BlockLoadingException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * Remove a block from the workspace.
     *
     * @param block The block block to remove, possibly with descendants attached.
     * @param cleanupStats True if this block is being deleted and its connections and references
     *                     should be removed.
     * @return True if the block was removed, false otherwise.
     */
    public boolean removeRootBlock(Block block, boolean cleanupStats) {
        boolean foundAndRemoved = mRootBlocks.remove(block);
        if (foundAndRemoved) {
            block.setEventWorkspaceId(null);
            if (cleanupStats) {
                mStats.cleanupStats(block);
            }
        }
        return foundAndRemoved;
    }

    /**
     * Add a root block to the trash.
     *
     * @param block The block to put in the trash, possibly with descendants attached.
     */
    // TODO(#56): Make sure the block doesn't have a parent.
    public void addBlockToTrash(Block block) {
        Log.i("HTS", "addBlockToTrash(Block block) => " + block.getType());

        BlocklyCategory.BlockItem blockItem = new BlocklyCategory.BlockItem(block);
        blockItem.getBlock().setEventWorkspaceId(BlocklyEvent.WORKSPACE_ID_TRASH);
        mTrashCategory.addItem(0, blockItem);

//        mDoingStackCategory.addItem(0, blockItem);

        Log.i("HTS", "blockItem => " + blockItem.getBlock().getType());
    }

    /**
     * Moves {@code trashedBlock} out of {@link #mTrashCategory} and into {@link #mRootBlocks}.
     *
     * @param trashedBlock The {@link Block} to move.
     * @throws IllegalArgumentException When {@code trashedBlock} is not found in
     *         {@link #mTrashCategory}.
     */
    public void addBlockFromTrash(Block trashedBlock) {
        Log.i("HTS", "addBlockFromTrash(Block trashedBlock) => " + trashedBlock.getType());
        boolean foundBlock = mTrashCategory.removeBlock(trashedBlock);
        if (!foundBlock) {
            throw new IllegalArgumentException("trashedBlock not found in mTrashCategory");
        }
        mRootBlocks.add(trashedBlock);
        trashedBlock.setEventWorkspaceId(getId());
    }

    /**
     * @return The {@link ConnectionManager} managing the connection locations in this Workspace.
     */
    public ConnectionManager getConnectionManager() {
        return mConnectionManager;
    }

    /**
     * Loads the toolbox category, blocks, and buttons from the {@code /raw/} resources directory.
     *
     * @param toolboxResId The resource id of the set of blocks or block groups to show in the
     * @throws BlockLoadingException If toolbox was not loaded. May wrap an IOException or another
     *                               BlockLoadingException.
     */
    public void loadToolboxContents(@RawRes int toolboxResId) throws BlockLoadingException {
        InputStream is = mContext.getResources().openRawResource(toolboxResId);
        loadToolboxContents(is);
    }

    /**
     * Loads the toolbox category, blocks, and buttons.
     *
     * @param source The source of the set of blocks or block groups to show in the toolbox.
     * @throws BlockLoadingException If toolbox was not loaded. May wrap an IOException or another
     *                               BlockLoadingException.
     */
    public void loadToolboxContents(InputStream source) throws BlockLoadingException {
        mFlyoutCategory = BlocklyXmlHelper.loadToolboxFromXml(source, mBlockFactory, BlocklyEvent.WORKSPACE_ID_TOOLBOX);
    }

    /**
     * Set up toolbox's contents.
     *
     * @param toolboxXml The xml of the set of blocks or block groups to show in the toolbox.
     * @throws BlockLoadingException If toolbox was not loaded. May wrap an IOException or another
     *                               BlockLoadingException.
     */
    public void loadToolboxContents(String toolboxXml) throws BlockLoadingException {
        loadToolboxContents(new ByteArrayInputStream(toolboxXml.getBytes()));
    }

    /**
     * Loads a list of blocks into the trash from an input stream. The trash is loaded like a
     * toolbox and can have a name, color, and set of blocks to start with. Unlike a toolbox it may
     * not have subcategories.
     *
     * @param source The source to initialize the trash.
     * @throws BlockLoadingException If trash was not loaded. May wrap an IOException or another
     *                               BlockLoadingException.
     */
    public void loadTrashContents(InputStream source) throws BlockLoadingException {
        mTrashCategory = BlocklyXmlHelper.loadToolboxFromXml(source, mBlockFactory, BlocklyEvent.WORKSPACE_ID_TRASH);
    }

    /**
     * Loads a list of blocks into the trash from an input stream. The trash is loaded like a
     * toolbox and can have a name, color, and set of blocks to start with. Unlike a toolbox it may
     * not have subcategories.
     *
     * @param trashXml The xml of the flyout to configure the trash.
     * @throws BlockLoadingException If trash was not loaded. May wrap an IOException or another
     *                               BlockLoadingException.
     */
    public void loadTrashContents(String trashXml) throws BlockLoadingException {
        loadTrashContents(new ByteArrayInputStream(trashXml.getBytes()));
    }


    /**
     * Reads the workspace in from a XML stream. This will clear the workspace and replace it with
     * the contents of the xml.
     *
     * @param is The input stream to read from.
     * @throws BlockLoadingException If workspace was not loaded. May wrap an IOException or another
     *                               BlockLoadingException.
     */
    public void loadWorkspaceContents(InputStream is) throws BlockLoadingException {
        List<Block> newBlocks = BlocklyXmlHelper.loadFromXml(is, mBlockFactory);

        // Successfully deserialized.  Update workspace.
        // TODO: (#22) Add proper variable support.
        // For now just save and restore the list of variables.
        Set<String> vars = mVariableNameManager.getUsedNames();
        mController.resetWorkspace();
        for (String varName : vars) {
            mController.addVariable(varName);
        }

        mRootBlocks.addAll(newBlocks);
        mStats.collectStats(newBlocks, true /* recursive */);
    }

    public List<Block> XmltoBlock(InputStream is) throws BlockLoadingException {
        List<Block> Blocks = BlocklyXmlHelper.loadFromXml(is, mBlockFactory);

        return Blocks;
    }

    /**
     * Reads the workspace in from a XML stream. This will clear the workspace and replace it with
     * the contents of the xml.
     *
     * @param xml The XML source string to read from.
     * @throws BlockLoadingException If toolbox was not loaded. May wrap an IOException or another
     *                               BlockLoadingException.
     */
    public void loadWorkspaceContents(String xml) throws BlockLoadingException {
        loadWorkspaceContents(new ByteArrayInputStream(xml.getBytes()));
    }

    /**
     * Gets the {@link BlockFactory} being used by this workspace. This can be used to update or
     * replace the set of known blocks.
     *
     * @return The block factory used by this workspace.
     */
    public BlockFactory getBlockFactory() {
        return mBlockFactory;
    }

    /**
     * Gets the {@link NameManager.VariableNameManager} being used by this workspace. This can be
     * used to get a list of variables in the workspace.
     *
     * @return The name manager for variables in this workspace.
     */
    public NameManager getVariableNameManager() {
        return mVariableNameManager;
    }

    /**
     * @return The {@link ProcedureManager} being used by this workspace.
     */
    public ProcedureManager getProcedureManager() {
        return mProcedureManager;
    }

    /**
     * Outputs the workspace as an XML string.
     *
     * @param os The output stream to write to.
     * @throws BlocklySerializerException if there was a failure while serializing.
     */
    public void serializeToXml(OutputStream os) throws BlocklySerializerException {

        // mRootBlocks를 정렬하여보자..
        Collections.sort(mRootBlocks, sortByXY);

        BlocklyXmlHelper.writeToXml(mRootBlocks, os, IOOptions.WRITE_ALL_DATA);
    }

    // x 좌표 먼저 정렬하고, 동률일때에는 Y값으로 정렬하도록 한다. @HTS
    private final static Comparator<Block> sortByXY = new Comparator<Block>() {
        @Override
        public int compare(Block b1, Block b2) {
            int ret = 0;
            int b1_x = (int) b1.getPosition().x;
            int b2_x = (int) b2.getPosition().x;
            if (b1_x < b2_x) {
                ret = Integer.compare(b1_x, b2_x);
            }
            if (b1_x == b2_x) {
                int b1_y = (int) b1.getPosition().y;
                int b2_y = (int) b2.getPosition().y;
                if (b1_y < b2_y) {
                    ret = Integer.compare(b1_y, b2_y);
                }
            }

            return ret;
//            return object1.getPosition().x < object2.getPosition().x ? -1 : (object1.getPosition().x == object2.getPosition().x) ? 0 : 1;
        }
    };

    /**
     * Reset the workspace view when changing workspaces.  Removes old views and creates all
     * necessary new views.
     */
    public void resetWorkspace() {
        mBlockFactory.clearWorkspaceBlockReferences(getId());
        mRootBlocks.clear();
        mStats.clear();
        mTrashCategory.clear();
    }

    public boolean hasDeletedBlocks() {
        return !mTrashCategory.getItems().isEmpty();
    }

    public BlocklyCategory getToolboxContents() {
        return mFlyoutCategory;
    }

    public BlocklyCategory getTrashCategory() { return mTrashCategory; }


    public ArrayList<Block> getRootBlocks() {
        return mRootBlocks;
    }


    public void setBlock_NextConnection(int Idx, Connection c) {
        mRootBlocks.get(Idx).getNextConnection().setTargetConnection(c);
    }

    public boolean isRootBlock(Block block) {
        return mRootBlocks.contains(block);
    }

    /**
     * @return if the workspace currently has any blocks.
     */
    public boolean hasBlocks() {
        return getRootBlocks().size() > 0;
    }

    /**
     * @param variable The variable name in question.
     * @return The usages of the variable, if any. Otherwise, null.
     */
    public @Nullable
    VariableInfo getVariableInfo(String variable) {
        return mStats.getVariableInfo(variable);
    }

    /**
     * Attempts to add a variable to the workspace.
     * @param requestedName The preferred variable name. Usually the user name.
     * @param allowRename Whether the variable name should be rename
     * @return The name that was added, if any. May be null if renaming is not allowed.
     */
    @Nullable
    public String addVariable(String requestedName, boolean allowRename) {
        return mStats.addVariable(requestedName, allowRename);
    }
}
