pragma solidity ^0.4.25;

contract MessageNotary {

    mapping (address => mapping (bytes32 => bool)) receivedMessages;

    function notarizeMessage(bytes32 hash) public {
        receivedMessages[msg.sender][hash] = true;
    }

    function verifyMessage(address recipient, bytes32 hash) public constant returns (bool) {
        return receivedMessages[recipient][hash];
    }
}