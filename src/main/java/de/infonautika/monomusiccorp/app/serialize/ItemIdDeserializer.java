package de.infonautika.monomusiccorp.app.serialize;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import de.infonautika.monomusiccorp.app.domain.ItemId;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ItemIdDeserializer extends JsonDeserializer<ItemId> {


    private JsonParser currentParser;

    @Override
    public ItemId deserialize(JsonParser currentParser, DeserializationContext ctxt) throws IOException {
        this.currentParser = currentParser;

        TreeNode treeNode = getObjectNode();
        TreeNode idNode = getIdNode(treeNode);
        return new ItemId(((TextNode) idNode).asText());
    }

    private TreeNode getIdNode(TreeNode treeNode) throws JsonParseException {
        TreeNode idNode = treeNode.get("itemId");
        checkIdNode(idNode);
        return idNode;
    }

    private TreeNode getObjectNode() throws IOException {
        TreeNode treeNode = currentParser.getCodec().readTree(currentParser);
        checkTreeNode(treeNode);
        return treeNode;
    }

    private void checkTreeNode(TreeNode treeNode) throws JsonParseException {
        checkForNull(treeNode, "object not given");
    }

    private void checkForNull(TreeNode treeNode, String msg) throws JsonParseException {
        if (treeNode == null) {
            throw new JsonParseException(currentParser, msg);
        }
    }

    private void checkIdNode(TreeNode idNode) throws JsonParseException {
        checkForNull(idNode, "itemId field not given");
        if (!(idNode instanceof TextNode)) {
            throw new JsonParseException(currentParser, "given id field is not a string");
        }
    }
}
