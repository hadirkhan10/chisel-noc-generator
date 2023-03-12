module MeshNode(
  input        clock,
  input        reset,
  input        io_in_1_valid,
  input  [8:0] io_in_1_bits_word_1,
  input        io_in_2_valid,
  input  [8:0] io_in_2_bits_word_1,
  output       io_requestPacket_ready,
  input        io_requestPacket_valid,
  input        io_requestPacket_bits_destNodeID_0,
  input        io_requestPacket_bits_destNodeID_1,
  output       io_out_1_valid,
  output [8:0] io_out_1_bits_word_1,
  output       io_out_2_valid,
  output [8:0] io_out_2_bits_word_1,
  output [2:0] io_state,
  output       io_routeLookup_destNodeID_0,
  output       io_routeLookup_destNodeID_1,
  input  [2:0] io_routeLookup_route_0,
  input  [2:0] io_routeLookup_route_1
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  reg [2:0] state; // @[MeshNode.scala 74:22]
  reg  isReady; // @[MeshNode.scala 75:24]
  reg [2:0] nextHop; // @[MeshNode.scala 76:24]
  reg [5:0] nextRoute; // @[MeshNode.scala 77:26]
  wire  _T_3 = io_requestPacket_ready & io_requestPacket_valid; // @[Decoupled.scala 52:35]
  wire [5:0] _nextRoute_T = {3'h4,io_routeLookup_route_1}; // @[MeshNode.scala 99:42]
  wire [1:0] _index_T_4 = io_in_2_valid ? 2'h2 : 2'h3; // @[MeshNode.scala 106:37]
  wire [1:0] index = io_in_1_valid ? 2'h1 : _index_T_4; // @[MeshNode.scala 106:37]
  wire [8:0] _GEN_1 = 2'h1 == index ? io_in_1_bits_word_1 : 9'h0; // @[MeshNode.scala 107:{54,54}]
  wire [8:0] _GEN_2 = 2'h2 == index ? io_in_2_bits_word_1 : _GEN_1; // @[MeshNode.scala 107:{54,54}]
  wire [8:0] _GEN_3 = 2'h3 == index ? 9'h0 : _GEN_2; // @[MeshNode.scala 107:{54,54}]
  wire [1:0] _nextHop_T_9 = {_GEN_3[1],_GEN_3[0]}; // @[MeshNode.scala 107:93]
  wire [4:0] _nextRoute_T_10 = {_GEN_3[7],_GEN_3[6],_GEN_3[5],_GEN_3[4],_GEN_3[3]}; // @[MeshNode.scala 108:110]
  wire  _GEN_10 = _T_3 ? 1'h0 : isReady; // @[MeshNode.scala 101:17 75:24 95:35]
  wire  _GEN_13 = 2'h1 == nextHop[1:0]; // @[MeshNode.scala 115:{29,29} 89:15]
  wire  _GEN_14 = 2'h2 == nextHop[1:0]; // @[MeshNode.scala 115:{29,29} 89:15]
  wire [8:0] header_word_1 = {{3'd0}, nextRoute}; // @[MeshNode.scala 148:22 150:20]
  wire [8:0] _GEN_21 = 2'h1 == nextHop[1:0] ? header_word_1 : 9'h0; // @[MeshNode.scala 116:{28,28} 90:34]
  wire [8:0] _GEN_22 = 2'h2 == nextHop[1:0] ? header_word_1 : 9'h0; // @[MeshNode.scala 116:{28,28} 90:34]
  wire  _state_T_2 = nextHop == 3'h4 ? 1'h0 : 1'h1; // @[MeshNode.scala 143:19]
  wire [2:0] _GEN_64 = 3'h6 == state ? {{2'd0}, _state_T_2} : state; // @[MeshNode.scala 143:13 93:18 74:22]
  wire [2:0] _GEN_66 = 3'h5 == state ? 3'h6 : _GEN_64; // @[MeshNode.scala 138:13 93:18]
  wire  _GEN_68 = 3'h3 == state & _GEN_13; // @[MeshNode.scala 89:15 93:18]
  wire  _GEN_69 = 3'h3 == state & _GEN_14; // @[MeshNode.scala 89:15 93:18]
  wire [2:0] _GEN_83 = 3'h3 == state ? 3'h0 : _GEN_66; // @[MeshNode.scala 131:13 93:18]
  wire  _GEN_84 = 3'h3 == state | isReady; // @[MeshNode.scala 132:15 93:18 75:24]
  wire  _GEN_87 = 3'h2 == state ? _GEN_13 : _GEN_68; // @[MeshNode.scala 93:18]
  wire  _GEN_88 = 3'h2 == state ? _GEN_14 : _GEN_69; // @[MeshNode.scala 93:18]
  wire  _GEN_103 = 3'h2 == state ? isReady : _GEN_84; // @[MeshNode.scala 93:18 75:24]
  wire  _GEN_106 = 3'h1 == state ? _GEN_13 : _GEN_87; // @[MeshNode.scala 93:18]
  wire  _GEN_107 = 3'h1 == state ? _GEN_14 : _GEN_88; // @[MeshNode.scala 93:18]
  wire [8:0] _GEN_114 = 3'h1 == state ? _GEN_21 : 9'h0; // @[MeshNode.scala 93:18]
  wire [8:0] _GEN_115 = 3'h1 == state ? _GEN_22 : 9'h0; // @[MeshNode.scala 93:18]
  wire  _GEN_122 = 3'h1 == state ? isReady : _GEN_103; // @[MeshNode.scala 93:18 75:24]
  wire  _GEN_127 = 3'h0 == state ? _GEN_10 : _GEN_122; // @[MeshNode.scala 93:18]
  assign io_requestPacket_ready = isReady; // @[MeshNode.scala 189:26]
  assign io_out_1_valid = 3'h0 == state ? 1'h0 : _GEN_106; // @[MeshNode.scala 89:15 93:18]
  assign io_out_1_bits_word_1 = 3'h0 == state ? 9'h0 : _GEN_114; // @[MeshNode.scala 93:18 90:34]
  assign io_out_2_valid = 3'h0 == state ? 1'h0 : _GEN_107; // @[MeshNode.scala 89:15 93:18]
  assign io_out_2_bits_word_1 = 3'h0 == state ? 9'h0 : _GEN_115; // @[MeshNode.scala 93:18 90:34]
  assign io_state = state; // @[MeshNode.scala 190:12]
  assign io_routeLookup_destNodeID_0 = io_requestPacket_bits_destNodeID_0; // @[MeshNode.scala 186:32]
  assign io_routeLookup_destNodeID_1 = io_requestPacket_bits_destNodeID_1; // @[MeshNode.scala 187:32]
  always @(posedge clock) begin
    if (reset) begin // @[MeshNode.scala 74:22]
      state <= 3'h0; // @[MeshNode.scala 74:22]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        state <= 3'h1; // @[MeshNode.scala 102:15]
      end else if (io_in_1_valid | io_in_2_valid) begin // @[MeshNode.scala 103:63]
        state <= 3'h5; // @[MeshNode.scala 110:15]
      end
    end else if (3'h1 == state) begin // @[MeshNode.scala 93:18]
      state <= 3'h2; // @[MeshNode.scala 117:13]
    end else if (3'h2 == state) begin // @[MeshNode.scala 93:18]
      state <= 3'h3; // @[MeshNode.scala 124:13]
    end else begin
      state <= _GEN_83;
    end
    isReady <= reset | _GEN_127; // @[MeshNode.scala 75:{24,24}]
    if (reset) begin // @[MeshNode.scala 76:24]
      nextHop <= 3'h0; // @[MeshNode.scala 76:24]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        nextHop <= io_routeLookup_route_0; // @[MeshNode.scala 98:17]
      end else if (io_in_1_valid | io_in_2_valid) begin // @[MeshNode.scala 103:63]
        nextHop <= {{1'd0}, _nextHop_T_9}; // @[MeshNode.scala 107:17]
      end
    end
    if (reset) begin // @[MeshNode.scala 77:26]
      nextRoute <= 6'h0; // @[MeshNode.scala 77:26]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        nextRoute <= _nextRoute_T; // @[MeshNode.scala 99:19]
      end else if (io_in_1_valid | io_in_2_valid) begin // @[MeshNode.scala 103:63]
        nextRoute <= {{1'd0}, _nextRoute_T_10}; // @[MeshNode.scala 108:19]
      end
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  state = _RAND_0[2:0];
  _RAND_1 = {1{`RANDOM}};
  isReady = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  nextHop = _RAND_2[2:0];
  _RAND_3 = {1{`RANDOM}};
  nextRoute = _RAND_3[5:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module MeshNode_1(
  input        clock,
  input        reset,
  input        io_in_1_valid,
  input  [8:0] io_in_1_bits_word_1,
  input        io_in_3_valid,
  input  [8:0] io_in_3_bits_word_1,
  output       io_requestPacket_ready,
  input        io_requestPacket_valid,
  input        io_requestPacket_bits_destNodeID_0,
  input        io_requestPacket_bits_destNodeID_1,
  output       io_out_1_valid,
  output [8:0] io_out_1_bits_word_1,
  output       io_out_3_valid,
  output [8:0] io_out_3_bits_word_1,
  output [2:0] io_state,
  output       io_routeLookup_destNodeID_0,
  output       io_routeLookup_destNodeID_1,
  input  [2:0] io_routeLookup_route_0,
  input  [2:0] io_routeLookup_route_1
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  reg [2:0] state; // @[MeshNode.scala 74:22]
  reg  isReady; // @[MeshNode.scala 75:24]
  reg [2:0] nextHop; // @[MeshNode.scala 76:24]
  reg [5:0] nextRoute; // @[MeshNode.scala 77:26]
  wire  _T_3 = io_requestPacket_ready & io_requestPacket_valid; // @[Decoupled.scala 52:35]
  wire [5:0] _nextRoute_T = {3'h4,io_routeLookup_route_1}; // @[MeshNode.scala 99:42]
  wire [1:0] index = io_in_1_valid ? 2'h1 : 2'h3; // @[MeshNode.scala 106:37]
  wire [8:0] _GEN_1 = 2'h1 == index ? io_in_1_bits_word_1 : 9'h0; // @[MeshNode.scala 107:{54,54}]
  wire [8:0] _GEN_2 = 2'h2 == index ? 9'h0 : _GEN_1; // @[MeshNode.scala 107:{54,54}]
  wire [8:0] _GEN_3 = 2'h3 == index ? io_in_3_bits_word_1 : _GEN_2; // @[MeshNode.scala 107:{54,54}]
  wire [1:0] _nextHop_T_9 = {_GEN_3[1],_GEN_3[0]}; // @[MeshNode.scala 107:93]
  wire [4:0] _nextRoute_T_10 = {_GEN_3[7],_GEN_3[6],_GEN_3[5],_GEN_3[4],_GEN_3[3]}; // @[MeshNode.scala 108:110]
  wire  _GEN_10 = _T_3 ? 1'h0 : isReady; // @[MeshNode.scala 101:17 75:24 95:35]
  wire  _GEN_13 = 2'h1 == nextHop[1:0]; // @[MeshNode.scala 115:{29,29} 89:15]
  wire  _GEN_15 = 2'h3 == nextHop[1:0]; // @[MeshNode.scala 115:{29,29} 89:15]
  wire [8:0] header_word_1 = {{3'd0}, nextRoute}; // @[MeshNode.scala 148:22 150:20]
  wire [8:0] _GEN_21 = 2'h1 == nextHop[1:0] ? header_word_1 : 9'h0; // @[MeshNode.scala 116:{28,28} 90:34]
  wire [8:0] _GEN_23 = 2'h3 == nextHop[1:0] ? header_word_1 : 9'h0; // @[MeshNode.scala 116:{28,28} 90:34]
  wire  _state_T_2 = nextHop == 3'h4 ? 1'h0 : 1'h1; // @[MeshNode.scala 143:19]
  wire [2:0] _GEN_64 = 3'h6 == state ? {{2'd0}, _state_T_2} : state; // @[MeshNode.scala 143:13 93:18 74:22]
  wire [2:0] _GEN_66 = 3'h5 == state ? 3'h6 : _GEN_64; // @[MeshNode.scala 138:13 93:18]
  wire  _GEN_68 = 3'h3 == state & _GEN_13; // @[MeshNode.scala 89:15 93:18]
  wire  _GEN_70 = 3'h3 == state & _GEN_15; // @[MeshNode.scala 89:15 93:18]
  wire [2:0] _GEN_83 = 3'h3 == state ? 3'h0 : _GEN_66; // @[MeshNode.scala 131:13 93:18]
  wire  _GEN_84 = 3'h3 == state | isReady; // @[MeshNode.scala 132:15 93:18 75:24]
  wire  _GEN_87 = 3'h2 == state ? _GEN_13 : _GEN_68; // @[MeshNode.scala 93:18]
  wire  _GEN_89 = 3'h2 == state ? _GEN_15 : _GEN_70; // @[MeshNode.scala 93:18]
  wire  _GEN_103 = 3'h2 == state ? isReady : _GEN_84; // @[MeshNode.scala 93:18 75:24]
  wire  _GEN_106 = 3'h1 == state ? _GEN_13 : _GEN_87; // @[MeshNode.scala 93:18]
  wire  _GEN_108 = 3'h1 == state ? _GEN_15 : _GEN_89; // @[MeshNode.scala 93:18]
  wire [8:0] _GEN_114 = 3'h1 == state ? _GEN_21 : 9'h0; // @[MeshNode.scala 93:18]
  wire [8:0] _GEN_116 = 3'h1 == state ? _GEN_23 : 9'h0; // @[MeshNode.scala 93:18]
  wire  _GEN_122 = 3'h1 == state ? isReady : _GEN_103; // @[MeshNode.scala 93:18 75:24]
  wire  _GEN_127 = 3'h0 == state ? _GEN_10 : _GEN_122; // @[MeshNode.scala 93:18]
  assign io_requestPacket_ready = isReady; // @[MeshNode.scala 189:26]
  assign io_out_1_valid = 3'h0 == state ? 1'h0 : _GEN_106; // @[MeshNode.scala 89:15 93:18]
  assign io_out_1_bits_word_1 = 3'h0 == state ? 9'h0 : _GEN_114; // @[MeshNode.scala 93:18 90:34]
  assign io_out_3_valid = 3'h0 == state ? 1'h0 : _GEN_108; // @[MeshNode.scala 89:15 93:18]
  assign io_out_3_bits_word_1 = 3'h0 == state ? 9'h0 : _GEN_116; // @[MeshNode.scala 93:18 90:34]
  assign io_state = state; // @[MeshNode.scala 190:12]
  assign io_routeLookup_destNodeID_0 = io_requestPacket_bits_destNodeID_0; // @[MeshNode.scala 186:32]
  assign io_routeLookup_destNodeID_1 = io_requestPacket_bits_destNodeID_1; // @[MeshNode.scala 187:32]
  always @(posedge clock) begin
    if (reset) begin // @[MeshNode.scala 74:22]
      state <= 3'h0; // @[MeshNode.scala 74:22]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        state <= 3'h1; // @[MeshNode.scala 102:15]
      end else if (io_in_1_valid | io_in_3_valid) begin // @[MeshNode.scala 103:63]
        state <= 3'h5; // @[MeshNode.scala 110:15]
      end
    end else if (3'h1 == state) begin // @[MeshNode.scala 93:18]
      state <= 3'h2; // @[MeshNode.scala 117:13]
    end else if (3'h2 == state) begin // @[MeshNode.scala 93:18]
      state <= 3'h3; // @[MeshNode.scala 124:13]
    end else begin
      state <= _GEN_83;
    end
    isReady <= reset | _GEN_127; // @[MeshNode.scala 75:{24,24}]
    if (reset) begin // @[MeshNode.scala 76:24]
      nextHop <= 3'h0; // @[MeshNode.scala 76:24]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        nextHop <= io_routeLookup_route_0; // @[MeshNode.scala 98:17]
      end else if (io_in_1_valid | io_in_3_valid) begin // @[MeshNode.scala 103:63]
        nextHop <= {{1'd0}, _nextHop_T_9}; // @[MeshNode.scala 107:17]
      end
    end
    if (reset) begin // @[MeshNode.scala 77:26]
      nextRoute <= 6'h0; // @[MeshNode.scala 77:26]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        nextRoute <= _nextRoute_T; // @[MeshNode.scala 99:19]
      end else if (io_in_1_valid | io_in_3_valid) begin // @[MeshNode.scala 103:63]
        nextRoute <= {{1'd0}, _nextRoute_T_10}; // @[MeshNode.scala 108:19]
      end
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  state = _RAND_0[2:0];
  _RAND_1 = {1{`RANDOM}};
  isReady = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  nextHop = _RAND_2[2:0];
  _RAND_3 = {1{`RANDOM}};
  nextRoute = _RAND_3[5:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module MeshNode_2(
  input        clock,
  input        reset,
  input        io_in_0_valid,
  input  [8:0] io_in_0_bits_word_1,
  input        io_in_2_valid,
  input  [8:0] io_in_2_bits_word_1,
  output       io_requestPacket_ready,
  input        io_requestPacket_valid,
  input        io_requestPacket_bits_destNodeID_0,
  input        io_requestPacket_bits_destNodeID_1,
  output       io_out_0_valid,
  output [8:0] io_out_0_bits_word_1,
  output       io_out_2_valid,
  output [8:0] io_out_2_bits_word_1,
  output [2:0] io_state,
  output       io_routeLookup_destNodeID_0,
  output       io_routeLookup_destNodeID_1,
  input  [2:0] io_routeLookup_route_0,
  input  [2:0] io_routeLookup_route_1
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  reg [2:0] state; // @[MeshNode.scala 74:22]
  reg  isReady; // @[MeshNode.scala 75:24]
  reg [2:0] nextHop; // @[MeshNode.scala 76:24]
  reg [5:0] nextRoute; // @[MeshNode.scala 77:26]
  wire  _T_3 = io_requestPacket_ready & io_requestPacket_valid; // @[Decoupled.scala 52:35]
  wire [5:0] _nextRoute_T = {3'h4,io_routeLookup_route_1}; // @[MeshNode.scala 99:42]
  wire [1:0] _index_T_4 = io_in_2_valid ? 2'h2 : 2'h3; // @[MeshNode.scala 106:37]
  wire [1:0] index = io_in_0_valid ? 2'h0 : _index_T_4; // @[MeshNode.scala 106:37]
  wire [8:0] _GEN_1 = 2'h1 == index ? 9'h0 : io_in_0_bits_word_1; // @[MeshNode.scala 107:{54,54}]
  wire [8:0] _GEN_2 = 2'h2 == index ? io_in_2_bits_word_1 : _GEN_1; // @[MeshNode.scala 107:{54,54}]
  wire [8:0] _GEN_3 = 2'h3 == index ? 9'h0 : _GEN_2; // @[MeshNode.scala 107:{54,54}]
  wire [1:0] _nextHop_T_9 = {_GEN_3[1],_GEN_3[0]}; // @[MeshNode.scala 107:93]
  wire [4:0] _nextRoute_T_10 = {_GEN_3[7],_GEN_3[6],_GEN_3[5],_GEN_3[4],_GEN_3[3]}; // @[MeshNode.scala 108:110]
  wire  _GEN_10 = _T_3 ? 1'h0 : isReady; // @[MeshNode.scala 101:17 75:24 95:35]
  wire  _GEN_12 = 2'h0 == nextHop[1:0]; // @[MeshNode.scala 115:{29,29} 89:15]
  wire  _GEN_14 = 2'h2 == nextHop[1:0]; // @[MeshNode.scala 115:{29,29} 89:15]
  wire [8:0] header_word_1 = {{3'd0}, nextRoute}; // @[MeshNode.scala 148:22 150:20]
  wire [8:0] _GEN_20 = 2'h0 == nextHop[1:0] ? header_word_1 : 9'h0; // @[MeshNode.scala 116:{28,28} 90:34]
  wire [8:0] _GEN_22 = 2'h2 == nextHop[1:0] ? header_word_1 : 9'h0; // @[MeshNode.scala 116:{28,28} 90:34]
  wire  _state_T_2 = nextHop == 3'h4 ? 1'h0 : 1'h1; // @[MeshNode.scala 143:19]
  wire [2:0] _GEN_64 = 3'h6 == state ? {{2'd0}, _state_T_2} : state; // @[MeshNode.scala 143:13 93:18 74:22]
  wire [2:0] _GEN_66 = 3'h5 == state ? 3'h6 : _GEN_64; // @[MeshNode.scala 138:13 93:18]
  wire  _GEN_67 = 3'h3 == state & _GEN_12; // @[MeshNode.scala 89:15 93:18]
  wire  _GEN_69 = 3'h3 == state & _GEN_14; // @[MeshNode.scala 89:15 93:18]
  wire [2:0] _GEN_83 = 3'h3 == state ? 3'h0 : _GEN_66; // @[MeshNode.scala 131:13 93:18]
  wire  _GEN_84 = 3'h3 == state | isReady; // @[MeshNode.scala 132:15 93:18 75:24]
  wire  _GEN_86 = 3'h2 == state ? _GEN_12 : _GEN_67; // @[MeshNode.scala 93:18]
  wire  _GEN_88 = 3'h2 == state ? _GEN_14 : _GEN_69; // @[MeshNode.scala 93:18]
  wire  _GEN_103 = 3'h2 == state ? isReady : _GEN_84; // @[MeshNode.scala 93:18 75:24]
  wire  _GEN_105 = 3'h1 == state ? _GEN_12 : _GEN_86; // @[MeshNode.scala 93:18]
  wire  _GEN_107 = 3'h1 == state ? _GEN_14 : _GEN_88; // @[MeshNode.scala 93:18]
  wire [8:0] _GEN_113 = 3'h1 == state ? _GEN_20 : 9'h0; // @[MeshNode.scala 93:18]
  wire [8:0] _GEN_115 = 3'h1 == state ? _GEN_22 : 9'h0; // @[MeshNode.scala 93:18]
  wire  _GEN_122 = 3'h1 == state ? isReady : _GEN_103; // @[MeshNode.scala 93:18 75:24]
  wire  _GEN_127 = 3'h0 == state ? _GEN_10 : _GEN_122; // @[MeshNode.scala 93:18]
  assign io_requestPacket_ready = isReady; // @[MeshNode.scala 189:26]
  assign io_out_0_valid = 3'h0 == state ? 1'h0 : _GEN_105; // @[MeshNode.scala 89:15 93:18]
  assign io_out_0_bits_word_1 = 3'h0 == state ? 9'h0 : _GEN_113; // @[MeshNode.scala 93:18 90:34]
  assign io_out_2_valid = 3'h0 == state ? 1'h0 : _GEN_107; // @[MeshNode.scala 89:15 93:18]
  assign io_out_2_bits_word_1 = 3'h0 == state ? 9'h0 : _GEN_115; // @[MeshNode.scala 93:18 90:34]
  assign io_state = state; // @[MeshNode.scala 190:12]
  assign io_routeLookup_destNodeID_0 = io_requestPacket_bits_destNodeID_0; // @[MeshNode.scala 186:32]
  assign io_routeLookup_destNodeID_1 = io_requestPacket_bits_destNodeID_1; // @[MeshNode.scala 187:32]
  always @(posedge clock) begin
    if (reset) begin // @[MeshNode.scala 74:22]
      state <= 3'h0; // @[MeshNode.scala 74:22]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        state <= 3'h1; // @[MeshNode.scala 102:15]
      end else if (io_in_0_valid | io_in_2_valid) begin // @[MeshNode.scala 103:63]
        state <= 3'h5; // @[MeshNode.scala 110:15]
      end
    end else if (3'h1 == state) begin // @[MeshNode.scala 93:18]
      state <= 3'h2; // @[MeshNode.scala 117:13]
    end else if (3'h2 == state) begin // @[MeshNode.scala 93:18]
      state <= 3'h3; // @[MeshNode.scala 124:13]
    end else begin
      state <= _GEN_83;
    end
    isReady <= reset | _GEN_127; // @[MeshNode.scala 75:{24,24}]
    if (reset) begin // @[MeshNode.scala 76:24]
      nextHop <= 3'h0; // @[MeshNode.scala 76:24]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        nextHop <= io_routeLookup_route_0; // @[MeshNode.scala 98:17]
      end else if (io_in_0_valid | io_in_2_valid) begin // @[MeshNode.scala 103:63]
        nextHop <= {{1'd0}, _nextHop_T_9}; // @[MeshNode.scala 107:17]
      end
    end
    if (reset) begin // @[MeshNode.scala 77:26]
      nextRoute <= 6'h0; // @[MeshNode.scala 77:26]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        nextRoute <= _nextRoute_T; // @[MeshNode.scala 99:19]
      end else if (io_in_0_valid | io_in_2_valid) begin // @[MeshNode.scala 103:63]
        nextRoute <= {{1'd0}, _nextRoute_T_10}; // @[MeshNode.scala 108:19]
      end
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  state = _RAND_0[2:0];
  _RAND_1 = {1{`RANDOM}};
  isReady = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  nextHop = _RAND_2[2:0];
  _RAND_3 = {1{`RANDOM}};
  nextRoute = _RAND_3[5:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module MeshNode_3(
  input        clock,
  input        reset,
  input        io_in_0_valid,
  input  [8:0] io_in_0_bits_word_1,
  input        io_in_3_valid,
  input  [8:0] io_in_3_bits_word_1,
  output       io_requestPacket_ready,
  input        io_requestPacket_valid,
  input        io_requestPacket_bits_destNodeID_0,
  input        io_requestPacket_bits_destNodeID_1,
  output       io_out_0_valid,
  output [8:0] io_out_0_bits_word_1,
  output       io_out_3_valid,
  output [8:0] io_out_3_bits_word_1,
  output [2:0] io_state,
  output       io_routeLookup_destNodeID_0,
  output       io_routeLookup_destNodeID_1,
  input  [2:0] io_routeLookup_route_0,
  input  [2:0] io_routeLookup_route_1
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  reg [2:0] state; // @[MeshNode.scala 74:22]
  reg  isReady; // @[MeshNode.scala 75:24]
  reg [2:0] nextHop; // @[MeshNode.scala 76:24]
  reg [5:0] nextRoute; // @[MeshNode.scala 77:26]
  wire  _T_3 = io_requestPacket_ready & io_requestPacket_valid; // @[Decoupled.scala 52:35]
  wire [5:0] _nextRoute_T = {3'h4,io_routeLookup_route_1}; // @[MeshNode.scala 99:42]
  wire [1:0] index = io_in_0_valid ? 2'h0 : 2'h3; // @[MeshNode.scala 106:37]
  wire [8:0] _GEN_1 = 2'h1 == index ? 9'h0 : io_in_0_bits_word_1; // @[MeshNode.scala 107:{54,54}]
  wire [8:0] _GEN_2 = 2'h2 == index ? 9'h0 : _GEN_1; // @[MeshNode.scala 107:{54,54}]
  wire [8:0] _GEN_3 = 2'h3 == index ? io_in_3_bits_word_1 : _GEN_2; // @[MeshNode.scala 107:{54,54}]
  wire [1:0] _nextHop_T_9 = {_GEN_3[1],_GEN_3[0]}; // @[MeshNode.scala 107:93]
  wire [4:0] _nextRoute_T_10 = {_GEN_3[7],_GEN_3[6],_GEN_3[5],_GEN_3[4],_GEN_3[3]}; // @[MeshNode.scala 108:110]
  wire  _GEN_10 = _T_3 ? 1'h0 : isReady; // @[MeshNode.scala 101:17 75:24 95:35]
  wire  _GEN_12 = 2'h0 == nextHop[1:0]; // @[MeshNode.scala 115:{29,29} 89:15]
  wire  _GEN_15 = 2'h3 == nextHop[1:0]; // @[MeshNode.scala 115:{29,29} 89:15]
  wire [8:0] header_word_1 = {{3'd0}, nextRoute}; // @[MeshNode.scala 148:22 150:20]
  wire [8:0] _GEN_20 = 2'h0 == nextHop[1:0] ? header_word_1 : 9'h0; // @[MeshNode.scala 116:{28,28} 90:34]
  wire [8:0] _GEN_23 = 2'h3 == nextHop[1:0] ? header_word_1 : 9'h0; // @[MeshNode.scala 116:{28,28} 90:34]
  wire  _state_T_2 = nextHop == 3'h4 ? 1'h0 : 1'h1; // @[MeshNode.scala 143:19]
  wire [2:0] _GEN_64 = 3'h6 == state ? {{2'd0}, _state_T_2} : state; // @[MeshNode.scala 143:13 93:18 74:22]
  wire [2:0] _GEN_66 = 3'h5 == state ? 3'h6 : _GEN_64; // @[MeshNode.scala 138:13 93:18]
  wire  _GEN_67 = 3'h3 == state & _GEN_12; // @[MeshNode.scala 89:15 93:18]
  wire  _GEN_70 = 3'h3 == state & _GEN_15; // @[MeshNode.scala 89:15 93:18]
  wire [2:0] _GEN_83 = 3'h3 == state ? 3'h0 : _GEN_66; // @[MeshNode.scala 131:13 93:18]
  wire  _GEN_84 = 3'h3 == state | isReady; // @[MeshNode.scala 132:15 93:18 75:24]
  wire  _GEN_86 = 3'h2 == state ? _GEN_12 : _GEN_67; // @[MeshNode.scala 93:18]
  wire  _GEN_89 = 3'h2 == state ? _GEN_15 : _GEN_70; // @[MeshNode.scala 93:18]
  wire  _GEN_103 = 3'h2 == state ? isReady : _GEN_84; // @[MeshNode.scala 93:18 75:24]
  wire  _GEN_105 = 3'h1 == state ? _GEN_12 : _GEN_86; // @[MeshNode.scala 93:18]
  wire  _GEN_108 = 3'h1 == state ? _GEN_15 : _GEN_89; // @[MeshNode.scala 93:18]
  wire [8:0] _GEN_113 = 3'h1 == state ? _GEN_20 : 9'h0; // @[MeshNode.scala 93:18]
  wire [8:0] _GEN_116 = 3'h1 == state ? _GEN_23 : 9'h0; // @[MeshNode.scala 93:18]
  wire  _GEN_122 = 3'h1 == state ? isReady : _GEN_103; // @[MeshNode.scala 93:18 75:24]
  wire  _GEN_127 = 3'h0 == state ? _GEN_10 : _GEN_122; // @[MeshNode.scala 93:18]
  assign io_requestPacket_ready = isReady; // @[MeshNode.scala 189:26]
  assign io_out_0_valid = 3'h0 == state ? 1'h0 : _GEN_105; // @[MeshNode.scala 89:15 93:18]
  assign io_out_0_bits_word_1 = 3'h0 == state ? 9'h0 : _GEN_113; // @[MeshNode.scala 93:18 90:34]
  assign io_out_3_valid = 3'h0 == state ? 1'h0 : _GEN_108; // @[MeshNode.scala 89:15 93:18]
  assign io_out_3_bits_word_1 = 3'h0 == state ? 9'h0 : _GEN_116; // @[MeshNode.scala 93:18 90:34]
  assign io_state = state; // @[MeshNode.scala 190:12]
  assign io_routeLookup_destNodeID_0 = io_requestPacket_bits_destNodeID_0; // @[MeshNode.scala 186:32]
  assign io_routeLookup_destNodeID_1 = io_requestPacket_bits_destNodeID_1; // @[MeshNode.scala 187:32]
  always @(posedge clock) begin
    if (reset) begin // @[MeshNode.scala 74:22]
      state <= 3'h0; // @[MeshNode.scala 74:22]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        state <= 3'h1; // @[MeshNode.scala 102:15]
      end else if (io_in_0_valid | io_in_3_valid) begin // @[MeshNode.scala 103:63]
        state <= 3'h5; // @[MeshNode.scala 110:15]
      end
    end else if (3'h1 == state) begin // @[MeshNode.scala 93:18]
      state <= 3'h2; // @[MeshNode.scala 117:13]
    end else if (3'h2 == state) begin // @[MeshNode.scala 93:18]
      state <= 3'h3; // @[MeshNode.scala 124:13]
    end else begin
      state <= _GEN_83;
    end
    isReady <= reset | _GEN_127; // @[MeshNode.scala 75:{24,24}]
    if (reset) begin // @[MeshNode.scala 76:24]
      nextHop <= 3'h0; // @[MeshNode.scala 76:24]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        nextHop <= io_routeLookup_route_0; // @[MeshNode.scala 98:17]
      end else if (io_in_0_valid | io_in_3_valid) begin // @[MeshNode.scala 103:63]
        nextHop <= {{1'd0}, _nextHop_T_9}; // @[MeshNode.scala 107:17]
      end
    end
    if (reset) begin // @[MeshNode.scala 77:26]
      nextRoute <= 6'h0; // @[MeshNode.scala 77:26]
    end else if (3'h0 == state) begin // @[MeshNode.scala 93:18]
      if (_T_3) begin // @[MeshNode.scala 95:35]
        nextRoute <= _nextRoute_T; // @[MeshNode.scala 99:19]
      end else if (io_in_0_valid | io_in_3_valid) begin // @[MeshNode.scala 103:63]
        nextRoute <= {{1'd0}, _nextRoute_T_10}; // @[MeshNode.scala 108:19]
      end
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  state = _RAND_0[2:0];
  _RAND_1 = {1{`RANDOM}};
  isReady = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  nextHop = _RAND_2[2:0];
  _RAND_3 = {1{`RANDOM}};
  nextRoute = _RAND_3[5:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module MeshNetwork(
  input         clock,
  input         reset,
  output        io_requestPacket_0_ready,
  input         io_requestPacket_0_valid,
  input         io_requestPacket_0_bits_destNodeID_0,
  input         io_requestPacket_0_bits_destNodeID_1,
  input  [15:0] io_requestPacket_0_bits_payload,
  output        io_requestPacket_1_ready,
  input         io_requestPacket_1_valid,
  input         io_requestPacket_1_bits_destNodeID_0,
  input         io_requestPacket_1_bits_destNodeID_1,
  input  [15:0] io_requestPacket_1_bits_payload,
  output        io_requestPacket_2_ready,
  input         io_requestPacket_2_valid,
  input         io_requestPacket_2_bits_destNodeID_0,
  input         io_requestPacket_2_bits_destNodeID_1,
  input  [15:0] io_requestPacket_2_bits_payload,
  output        io_requestPacket_3_ready,
  input         io_requestPacket_3_valid,
  input         io_requestPacket_3_bits_destNodeID_0,
  input         io_requestPacket_3_bits_destNodeID_1,
  input  [15:0] io_requestPacket_3_bits_payload,
  output [2:0]  io_state_0,
  output [2:0]  io_state_1,
  output [2:0]  io_state_2,
  output [2:0]  io_state_3
);
  wire  mesh_0_0_clock; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_0_reset; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_0_io_in_1_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_0_0_io_in_1_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_0_io_in_2_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_0_0_io_in_2_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_0_io_requestPacket_ready; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_0_io_requestPacket_valid; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_0_io_requestPacket_bits_destNodeID_0; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_0_io_requestPacket_bits_destNodeID_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_0_io_out_1_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_0_0_io_out_1_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_0_io_out_2_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_0_0_io_out_2_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_0_0_io_state; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_0_io_routeLookup_destNodeID_0; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_0_io_routeLookup_destNodeID_1; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_0_0_io_routeLookup_route_0; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_0_0_io_routeLookup_route_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_clock; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_reset; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_io_in_1_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_0_1_io_in_1_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_io_in_3_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_0_1_io_in_3_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_io_requestPacket_ready; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_io_requestPacket_valid; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_io_requestPacket_bits_destNodeID_0; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_io_requestPacket_bits_destNodeID_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_io_out_1_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_0_1_io_out_1_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_io_out_3_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_0_1_io_out_3_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_0_1_io_state; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_io_routeLookup_destNodeID_0; // @[MeshNetwork.scala 62:66]
  wire  mesh_0_1_io_routeLookup_destNodeID_1; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_0_1_io_routeLookup_route_0; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_0_1_io_routeLookup_route_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_clock; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_reset; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_io_in_0_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_1_0_io_in_0_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_io_in_2_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_1_0_io_in_2_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_io_requestPacket_ready; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_io_requestPacket_valid; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_io_requestPacket_bits_destNodeID_0; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_io_requestPacket_bits_destNodeID_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_io_out_0_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_1_0_io_out_0_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_io_out_2_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_1_0_io_out_2_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_1_0_io_state; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_io_routeLookup_destNodeID_0; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_0_io_routeLookup_destNodeID_1; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_1_0_io_routeLookup_route_0; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_1_0_io_routeLookup_route_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_clock; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_reset; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_io_in_0_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_1_1_io_in_0_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_io_in_3_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_1_1_io_in_3_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_io_requestPacket_ready; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_io_requestPacket_valid; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_io_requestPacket_bits_destNodeID_0; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_io_requestPacket_bits_destNodeID_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_io_out_0_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_1_1_io_out_0_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_io_out_3_valid; // @[MeshNetwork.scala 62:66]
  wire [8:0] mesh_1_1_io_out_3_bits_word_1; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_1_1_io_state; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_io_routeLookup_destNodeID_0; // @[MeshNetwork.scala 62:66]
  wire  mesh_1_1_io_routeLookup_destNodeID_1; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_1_1_io_routeLookup_route_0; // @[MeshNetwork.scala 62:66]
  wire [2:0] mesh_1_1_io_routeLookup_route_1; // @[MeshNetwork.scala 62:66]
  wire [2:0] _GEN_1 = ~mesh_0_0_io_routeLookup_destNodeID_1 & mesh_0_0_io_routeLookup_destNodeID_0 ? 3'h2 : 3'h4; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_2 = mesh_0_0_io_routeLookup_destNodeID_1 & ~mesh_0_0_io_routeLookup_destNodeID_0 ? 3'h1 : _GEN_1; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_52 = ~mesh_0_1_io_routeLookup_destNodeID_1 & ~mesh_0_1_io_routeLookup_destNodeID_0 ? 3'h3 : 3'h4; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_53 = ~mesh_0_1_io_routeLookup_destNodeID_1 & mesh_0_1_io_routeLookup_destNodeID_0 ? 3'h4 : _GEN_52; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_54 = mesh_0_1_io_routeLookup_destNodeID_1 & ~mesh_0_1_io_routeLookup_destNodeID_0 ? 3'h3 : _GEN_53; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_70 = mesh_0_1_io_routeLookup_destNodeID_1 & ~mesh_0_1_io_routeLookup_destNodeID_0 ? 3'h1 : 3'h4; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_104 = ~mesh_1_0_io_routeLookup_destNodeID_1 & ~mesh_1_0_io_routeLookup_destNodeID_0 ? 3'h0 : 3'h4; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_105 = ~mesh_1_0_io_routeLookup_destNodeID_1 & mesh_1_0_io_routeLookup_destNodeID_0 ? 3'h2 : _GEN_104; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_106 = mesh_1_0_io_routeLookup_destNodeID_1 & ~mesh_1_0_io_routeLookup_destNodeID_0 ? 3'h4 : _GEN_105; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_121 = ~mesh_1_0_io_routeLookup_destNodeID_1 & mesh_1_0_io_routeLookup_destNodeID_0 ? 3'h0 : 3'h4; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_122 = mesh_1_0_io_routeLookup_destNodeID_1 & ~mesh_1_0_io_routeLookup_destNodeID_0 ? 3'h4 : _GEN_121; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_156 = ~mesh_1_1_io_routeLookup_destNodeID_1 & ~mesh_1_1_io_routeLookup_destNodeID_0 ? 3'h3 : 3'h4; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_157 = ~mesh_1_1_io_routeLookup_destNodeID_1 & mesh_1_1_io_routeLookup_destNodeID_0 ? 3'h0 : _GEN_156; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_158 = mesh_1_1_io_routeLookup_destNodeID_1 & ~mesh_1_1_io_routeLookup_destNodeID_0 ? 3'h3 : _GEN_157; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_172 = ~mesh_1_1_io_routeLookup_destNodeID_1 & ~mesh_1_1_io_routeLookup_destNodeID_0 ? 3'h0 : 3'h4; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_173 = ~mesh_1_1_io_routeLookup_destNodeID_1 & mesh_1_1_io_routeLookup_destNodeID_0 ? 3'h4 : _GEN_172; // @[MeshNetwork.scala 77:{31,31}]
  wire [2:0] _GEN_174 = mesh_1_1_io_routeLookup_destNodeID_1 & ~mesh_1_1_io_routeLookup_destNodeID_0 ? 3'h4 : _GEN_173; // @[MeshNetwork.scala 77:{31,31}]
  MeshNode mesh_0_0 ( // @[MeshNetwork.scala 62:66]
    .clock(mesh_0_0_clock),
    .reset(mesh_0_0_reset),
    .io_in_1_valid(mesh_0_0_io_in_1_valid),
    .io_in_1_bits_word_1(mesh_0_0_io_in_1_bits_word_1),
    .io_in_2_valid(mesh_0_0_io_in_2_valid),
    .io_in_2_bits_word_1(mesh_0_0_io_in_2_bits_word_1),
    .io_requestPacket_ready(mesh_0_0_io_requestPacket_ready),
    .io_requestPacket_valid(mesh_0_0_io_requestPacket_valid),
    .io_requestPacket_bits_destNodeID_0(mesh_0_0_io_requestPacket_bits_destNodeID_0),
    .io_requestPacket_bits_destNodeID_1(mesh_0_0_io_requestPacket_bits_destNodeID_1),
    .io_out_1_valid(mesh_0_0_io_out_1_valid),
    .io_out_1_bits_word_1(mesh_0_0_io_out_1_bits_word_1),
    .io_out_2_valid(mesh_0_0_io_out_2_valid),
    .io_out_2_bits_word_1(mesh_0_0_io_out_2_bits_word_1),
    .io_state(mesh_0_0_io_state),
    .io_routeLookup_destNodeID_0(mesh_0_0_io_routeLookup_destNodeID_0),
    .io_routeLookup_destNodeID_1(mesh_0_0_io_routeLookup_destNodeID_1),
    .io_routeLookup_route_0(mesh_0_0_io_routeLookup_route_0),
    .io_routeLookup_route_1(mesh_0_0_io_routeLookup_route_1)
  );
  MeshNode_1 mesh_0_1 ( // @[MeshNetwork.scala 62:66]
    .clock(mesh_0_1_clock),
    .reset(mesh_0_1_reset),
    .io_in_1_valid(mesh_0_1_io_in_1_valid),
    .io_in_1_bits_word_1(mesh_0_1_io_in_1_bits_word_1),
    .io_in_3_valid(mesh_0_1_io_in_3_valid),
    .io_in_3_bits_word_1(mesh_0_1_io_in_3_bits_word_1),
    .io_requestPacket_ready(mesh_0_1_io_requestPacket_ready),
    .io_requestPacket_valid(mesh_0_1_io_requestPacket_valid),
    .io_requestPacket_bits_destNodeID_0(mesh_0_1_io_requestPacket_bits_destNodeID_0),
    .io_requestPacket_bits_destNodeID_1(mesh_0_1_io_requestPacket_bits_destNodeID_1),
    .io_out_1_valid(mesh_0_1_io_out_1_valid),
    .io_out_1_bits_word_1(mesh_0_1_io_out_1_bits_word_1),
    .io_out_3_valid(mesh_0_1_io_out_3_valid),
    .io_out_3_bits_word_1(mesh_0_1_io_out_3_bits_word_1),
    .io_state(mesh_0_1_io_state),
    .io_routeLookup_destNodeID_0(mesh_0_1_io_routeLookup_destNodeID_0),
    .io_routeLookup_destNodeID_1(mesh_0_1_io_routeLookup_destNodeID_1),
    .io_routeLookup_route_0(mesh_0_1_io_routeLookup_route_0),
    .io_routeLookup_route_1(mesh_0_1_io_routeLookup_route_1)
  );
  MeshNode_2 mesh_1_0 ( // @[MeshNetwork.scala 62:66]
    .clock(mesh_1_0_clock),
    .reset(mesh_1_0_reset),
    .io_in_0_valid(mesh_1_0_io_in_0_valid),
    .io_in_0_bits_word_1(mesh_1_0_io_in_0_bits_word_1),
    .io_in_2_valid(mesh_1_0_io_in_2_valid),
    .io_in_2_bits_word_1(mesh_1_0_io_in_2_bits_word_1),
    .io_requestPacket_ready(mesh_1_0_io_requestPacket_ready),
    .io_requestPacket_valid(mesh_1_0_io_requestPacket_valid),
    .io_requestPacket_bits_destNodeID_0(mesh_1_0_io_requestPacket_bits_destNodeID_0),
    .io_requestPacket_bits_destNodeID_1(mesh_1_0_io_requestPacket_bits_destNodeID_1),
    .io_out_0_valid(mesh_1_0_io_out_0_valid),
    .io_out_0_bits_word_1(mesh_1_0_io_out_0_bits_word_1),
    .io_out_2_valid(mesh_1_0_io_out_2_valid),
    .io_out_2_bits_word_1(mesh_1_0_io_out_2_bits_word_1),
    .io_state(mesh_1_0_io_state),
    .io_routeLookup_destNodeID_0(mesh_1_0_io_routeLookup_destNodeID_0),
    .io_routeLookup_destNodeID_1(mesh_1_0_io_routeLookup_destNodeID_1),
    .io_routeLookup_route_0(mesh_1_0_io_routeLookup_route_0),
    .io_routeLookup_route_1(mesh_1_0_io_routeLookup_route_1)
  );
  MeshNode_3 mesh_1_1 ( // @[MeshNetwork.scala 62:66]
    .clock(mesh_1_1_clock),
    .reset(mesh_1_1_reset),
    .io_in_0_valid(mesh_1_1_io_in_0_valid),
    .io_in_0_bits_word_1(mesh_1_1_io_in_0_bits_word_1),
    .io_in_3_valid(mesh_1_1_io_in_3_valid),
    .io_in_3_bits_word_1(mesh_1_1_io_in_3_bits_word_1),
    .io_requestPacket_ready(mesh_1_1_io_requestPacket_ready),
    .io_requestPacket_valid(mesh_1_1_io_requestPacket_valid),
    .io_requestPacket_bits_destNodeID_0(mesh_1_1_io_requestPacket_bits_destNodeID_0),
    .io_requestPacket_bits_destNodeID_1(mesh_1_1_io_requestPacket_bits_destNodeID_1),
    .io_out_0_valid(mesh_1_1_io_out_0_valid),
    .io_out_0_bits_word_1(mesh_1_1_io_out_0_bits_word_1),
    .io_out_3_valid(mesh_1_1_io_out_3_valid),
    .io_out_3_bits_word_1(mesh_1_1_io_out_3_bits_word_1),
    .io_state(mesh_1_1_io_state),
    .io_routeLookup_destNodeID_0(mesh_1_1_io_routeLookup_destNodeID_0),
    .io_routeLookup_destNodeID_1(mesh_1_1_io_routeLookup_destNodeID_1),
    .io_routeLookup_route_0(mesh_1_1_io_routeLookup_route_0),
    .io_routeLookup_route_1(mesh_1_1_io_routeLookup_route_1)
  );
  assign io_requestPacket_0_ready = mesh_0_0_io_requestPacket_ready; // @[MeshNetwork.scala 74:27]
  assign io_requestPacket_1_ready = mesh_0_1_io_requestPacket_ready; // @[MeshNetwork.scala 74:27]
  assign io_requestPacket_2_ready = mesh_1_0_io_requestPacket_ready; // @[MeshNetwork.scala 74:27]
  assign io_requestPacket_3_ready = mesh_1_1_io_requestPacket_ready; // @[MeshNetwork.scala 74:27]
  assign io_state_0 = mesh_0_0_io_state; // @[MeshNetwork.scala 75:7]
  assign io_state_1 = mesh_0_1_io_state; // @[MeshNetwork.scala 75:7]
  assign io_state_2 = mesh_1_0_io_state; // @[MeshNetwork.scala 75:7]
  assign io_state_3 = mesh_1_1_io_state; // @[MeshNetwork.scala 75:7]
  assign mesh_0_0_clock = clock;
  assign mesh_0_0_reset = reset;
  assign mesh_0_0_io_in_1_valid = mesh_1_0_io_out_0_valid; // @[MeshNetwork.scala 125:35]
  assign mesh_0_0_io_in_1_bits_word_1 = mesh_1_0_io_out_0_bits_word_1; // @[MeshNetwork.scala 125:35]
  assign mesh_0_0_io_in_2_valid = mesh_0_1_io_out_3_valid; // @[MeshNetwork.scala 96:35]
  assign mesh_0_0_io_in_2_bits_word_1 = mesh_0_1_io_out_3_bits_word_1; // @[MeshNetwork.scala 96:35]
  assign mesh_0_0_io_requestPacket_valid = io_requestPacket_0_valid; // @[MeshNetwork.scala 74:27]
  assign mesh_0_0_io_requestPacket_bits_destNodeID_0 = io_requestPacket_0_bits_destNodeID_0; // @[MeshNetwork.scala 74:27]
  assign mesh_0_0_io_requestPacket_bits_destNodeID_1 = io_requestPacket_0_bits_destNodeID_1; // @[MeshNetwork.scala 74:27]
  assign mesh_0_0_io_routeLookup_route_0 = mesh_0_0_io_routeLookup_destNodeID_1 & mesh_0_0_io_routeLookup_destNodeID_0
     ? 3'h2 : _GEN_2; // @[MeshNetwork.scala 77:{31,31}]
  assign mesh_0_0_io_routeLookup_route_1 = mesh_0_0_io_routeLookup_destNodeID_1 & mesh_0_0_io_routeLookup_destNodeID_0
     ? 3'h1 : 3'h4; // @[MeshNetwork.scala 77:{31,31}]
  assign mesh_0_1_clock = clock;
  assign mesh_0_1_reset = reset;
  assign mesh_0_1_io_in_1_valid = mesh_1_1_io_out_0_valid; // @[MeshNetwork.scala 125:35]
  assign mesh_0_1_io_in_1_bits_word_1 = mesh_1_1_io_out_0_bits_word_1; // @[MeshNetwork.scala 125:35]
  assign mesh_0_1_io_in_3_valid = mesh_0_0_io_out_2_valid; // @[MeshNetwork.scala 97:37]
  assign mesh_0_1_io_in_3_bits_word_1 = mesh_0_0_io_out_2_bits_word_1; // @[MeshNetwork.scala 97:37]
  assign mesh_0_1_io_requestPacket_valid = io_requestPacket_1_valid; // @[MeshNetwork.scala 74:27]
  assign mesh_0_1_io_requestPacket_bits_destNodeID_0 = io_requestPacket_1_bits_destNodeID_0; // @[MeshNetwork.scala 74:27]
  assign mesh_0_1_io_requestPacket_bits_destNodeID_1 = io_requestPacket_1_bits_destNodeID_1; // @[MeshNetwork.scala 74:27]
  assign mesh_0_1_io_routeLookup_route_0 = mesh_0_1_io_routeLookup_destNodeID_1 & mesh_0_1_io_routeLookup_destNodeID_0
     ? 3'h1 : _GEN_54; // @[MeshNetwork.scala 77:{31,31}]
  assign mesh_0_1_io_routeLookup_route_1 = mesh_0_1_io_routeLookup_destNodeID_1 & mesh_0_1_io_routeLookup_destNodeID_0
     ? 3'h4 : _GEN_70; // @[MeshNetwork.scala 77:{31,31}]
  assign mesh_1_0_clock = clock;
  assign mesh_1_0_reset = reset;
  assign mesh_1_0_io_in_0_valid = mesh_0_0_io_out_1_valid; // @[MeshNetwork.scala 126:45]
  assign mesh_1_0_io_in_0_bits_word_1 = mesh_0_0_io_out_1_bits_word_1; // @[MeshNetwork.scala 126:45]
  assign mesh_1_0_io_in_2_valid = mesh_1_1_io_out_3_valid; // @[MeshNetwork.scala 96:35]
  assign mesh_1_0_io_in_2_bits_word_1 = mesh_1_1_io_out_3_bits_word_1; // @[MeshNetwork.scala 96:35]
  assign mesh_1_0_io_requestPacket_valid = io_requestPacket_2_valid; // @[MeshNetwork.scala 74:27]
  assign mesh_1_0_io_requestPacket_bits_destNodeID_0 = io_requestPacket_2_bits_destNodeID_0; // @[MeshNetwork.scala 74:27]
  assign mesh_1_0_io_requestPacket_bits_destNodeID_1 = io_requestPacket_2_bits_destNodeID_1; // @[MeshNetwork.scala 74:27]
  assign mesh_1_0_io_routeLookup_route_0 = mesh_1_0_io_routeLookup_destNodeID_1 & mesh_1_0_io_routeLookup_destNodeID_0
     ? 3'h2 : _GEN_106; // @[MeshNetwork.scala 77:{31,31}]
  assign mesh_1_0_io_routeLookup_route_1 = mesh_1_0_io_routeLookup_destNodeID_1 & mesh_1_0_io_routeLookup_destNodeID_0
     ? 3'h4 : _GEN_122; // @[MeshNetwork.scala 77:{31,31}]
  assign mesh_1_1_clock = clock;
  assign mesh_1_1_reset = reset;
  assign mesh_1_1_io_in_0_valid = mesh_0_1_io_out_1_valid; // @[MeshNetwork.scala 126:45]
  assign mesh_1_1_io_in_0_bits_word_1 = mesh_0_1_io_out_1_bits_word_1; // @[MeshNetwork.scala 126:45]
  assign mesh_1_1_io_in_3_valid = mesh_1_0_io_out_2_valid; // @[MeshNetwork.scala 97:37]
  assign mesh_1_1_io_in_3_bits_word_1 = mesh_1_0_io_out_2_bits_word_1; // @[MeshNetwork.scala 97:37]
  assign mesh_1_1_io_requestPacket_valid = io_requestPacket_3_valid; // @[MeshNetwork.scala 74:27]
  assign mesh_1_1_io_requestPacket_bits_destNodeID_0 = io_requestPacket_3_bits_destNodeID_0; // @[MeshNetwork.scala 74:27]
  assign mesh_1_1_io_requestPacket_bits_destNodeID_1 = io_requestPacket_3_bits_destNodeID_1; // @[MeshNetwork.scala 74:27]
  assign mesh_1_1_io_routeLookup_route_0 = mesh_1_1_io_routeLookup_destNodeID_1 & mesh_1_1_io_routeLookup_destNodeID_0
     ? 3'h4 : _GEN_158; // @[MeshNetwork.scala 77:{31,31}]
  assign mesh_1_1_io_routeLookup_route_1 = mesh_1_1_io_routeLookup_destNodeID_1 & mesh_1_1_io_routeLookup_destNodeID_0
     ? 3'h4 : _GEN_174; // @[MeshNetwork.scala 77:{31,31}]
endmodule
