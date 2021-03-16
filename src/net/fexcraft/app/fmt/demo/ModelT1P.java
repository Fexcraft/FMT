package net.fexcraft.app.fmt.demo;

import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.tmt.ModelBase;
import net.fexcraft.lib.tmt.ModelRendererTurbo;


/**
 * @author Ferdinand Calo' (FEX___96)
**/
public class ModelT1P extends ModelBase {
	
	public static final ModelT1P INSTANCE = new ModelT1P();
	public static RGB COLOR = new RGB(RGB.RED);
	private ModelRendererTurbo[] body, body_door_open_colored_primary, body_door_close_colored_primary, body_colored_primary, body_colored_secondary;

    public ModelT1P(){
    	super(); int textureX = 512, textureY = 512;
        //addToCreators("Ferdinand (FEX___96)");
        body = new ModelRendererTurbo[187];
        body[0] = new ModelRendererTurbo(this, 1, 1, textureX, textureY); // Box 0
        body[1] = new ModelRendererTurbo(this, 161, 1, textureX, textureY); // Box 1
        body[2] = new ModelRendererTurbo(this, 193, 1, textureX, textureY); // Box 2
        body[3] = new ModelRendererTurbo(this, 225, 1, textureX, textureY); // Box 3
        body[4] = new ModelRendererTurbo(this, 257, 1, textureX, textureY); // Box 4
        body[5] = new ModelRendererTurbo(this, 265, 1, textureX, textureY); // Box 5
        body[6] = new ModelRendererTurbo(this, 329, 1, textureX, textureY); // Box 6
        body[7] = new ModelRendererTurbo(this, 377, 1, textureX, textureY); // Box 7
        body[8] = new ModelRendererTurbo(this, 409, 1, textureX, textureY); // Box 8
        body[9] = new ModelRendererTurbo(this, 1, 9, textureX, textureY); // Box 9
        body[10] = new ModelRendererTurbo(this, 441, 1, textureX, textureY); // Box 10
        body[11] = new ModelRendererTurbo(this, 473, 1, textureX, textureY); // Box 11
        body[12] = new ModelRendererTurbo(this, 257, 65, textureX, textureY); // Box 100
        body[13] = new ModelRendererTurbo(this, 305, 65, textureX, textureY); // Box 101
        body[14] = new ModelRendererTurbo(this, 305, 65, textureX, textureY); // Box 102
        body[15] = new ModelRendererTurbo(this, 329, 65, textureX, textureY); // Box 103
        body[16] = new ModelRendererTurbo(this, 369, 65, textureX, textureY); // Box 104
        body[17] = new ModelRendererTurbo(this, 369, 65, textureX, textureY); // Box 105
        body[18] = new ModelRendererTurbo(this, 385, 65, textureX, textureY); // Box 106
        body[19] = new ModelRendererTurbo(this, 417, 65, textureX, textureY); // Box 107
        body[20] = new ModelRendererTurbo(this, 433, 65, textureX, textureY); // Box 108
        body[21] = new ModelRendererTurbo(this, 417, 65, textureX, textureY); // Box 109
        body[22] = new ModelRendererTurbo(this, 449, 65, textureX, textureY); // Box 110
        body[23] = new ModelRendererTurbo(this, 473, 65, textureX, textureY); // Box 111
        body[24] = new ModelRendererTurbo(this, 489, 65, textureX, textureY); // Box 112
        body[25] = new ModelRendererTurbo(this, 1, 73, textureX, textureY); // Box 113
        body[26] = new ModelRendererTurbo(this, 313, 65, textureX, textureY); // Box 114
        body[27] = new ModelRendererTurbo(this, 433, 65, textureX, textureY); // Box 115
        body[28] = new ModelRendererTurbo(this, 473, 65, textureX, textureY); // Box 116
        body[29] = new ModelRendererTurbo(this, 17, 73, textureX, textureY); // Box 117
        body[30] = new ModelRendererTurbo(this, 209, 73, textureX, textureY); // Box 118
        body[31] = new ModelRendererTurbo(this, 49, 81, textureX, textureY); // Box 119
        body[32] = new ModelRendererTurbo(this, 97, 73, textureX, textureY); // Box 121
        body[33] = new ModelRendererTurbo(this, 369, 73, textureX, textureY); // Box 123
        body[34] = new ModelRendererTurbo(this, 153, 73, textureX, textureY); // Box 129
        body[35] = new ModelRendererTurbo(this, 185, 89, textureX, textureY); // Box 139
        body[36] = new ModelRendererTurbo(this, 393, 113, textureX, textureY); // Box 140
        body[37] = new ModelRendererTurbo(this, 329, 81, textureX, textureY); // Box 142
        body[38] = new ModelRendererTurbo(this, 369, 81, textureX, textureY); // Box 143
        body[39] = new ModelRendererTurbo(this, 369, 89, textureX, textureY); // Box 149
        body[40] = new ModelRendererTurbo(this, 337, 89, textureX, textureY); // Box 150
        body[41] = new ModelRendererTurbo(this, 33, 97, textureX, textureY); // Box 151
        body[42] = new ModelRendererTurbo(this, 305, 121, textureX, textureY); // Box 153
        body[43] = new ModelRendererTurbo(this, 97, 129, textureX, textureY); // Box 154
        body[44] = new ModelRendererTurbo(this, 353, 129, textureX, textureY); // Box 155
        body[45] = new ModelRendererTurbo(this, 137, 137, textureX, textureY); // Box 156
        body[46] = new ModelRendererTurbo(this, 177, 145, textureX, textureY); // Box 157
        body[47] = new ModelRendererTurbo(this, 393, 145, textureX, textureY); // Box 158
        body[48] = new ModelRendererTurbo(this, 1, 153, textureX, textureY); // Box 159
        body[49] = new ModelRendererTurbo(this, 433, 153, textureX, textureY); // Box 160
        body[50] = new ModelRendererTurbo(this, 41, 161, textureX, textureY); // Box 161
        body[51] = new ModelRendererTurbo(this, 313, 161, textureX, textureY); // Box 162
        body[52] = new ModelRendererTurbo(this, 81, 169, textureX, textureY); // Box 163
        body[53] = new ModelRendererTurbo(this, 217, 169, textureX, textureY); // Box 164
        body[54] = new ModelRendererTurbo(this, 353, 169, textureX, textureY); // Box 165
        body[55] = new ModelRendererTurbo(this, 121, 177, textureX, textureY); // Box 166
        body[56] = new ModelRendererTurbo(this, 257, 177, textureX, textureY); // Box 167
        body[57] = new ModelRendererTurbo(this, 161, 185, textureX, textureY); // Box 168
        body[58] = new ModelRendererTurbo(this, 393, 185, textureX, textureY); // Box 169
        body[59] = new ModelRendererTurbo(this, 1, 193, textureX, textureY); // Box 170
        body[60] = new ModelRendererTurbo(this, 433, 193, textureX, textureY); // Box 171
        body[61] = new ModelRendererTurbo(this, 41, 201, textureX, textureY); // Box 172
        body[62] = new ModelRendererTurbo(this, 297, 201, textureX, textureY); // Box 173
        body[63] = new ModelRendererTurbo(this, 401, 65, textureX, textureY); // Box 174
        body[64] = new ModelRendererTurbo(this, 345, 73, textureX, textureY); // Box 175
        body[65] = new ModelRendererTurbo(this, 145, 81, textureX, textureY); // Box 176
        body[66] = new ModelRendererTurbo(this, 401, 81, textureX, textureY); // Box 177
        body[67] = new ModelRendererTurbo(this, 417, 81, textureX, textureY); // Box 178
        body[68] = new ModelRendererTurbo(this, 89, 105, textureX, textureY); // Box 179
        body[69] = new ModelRendererTurbo(this, 1, 113, textureX, textureY); // Box 182
        body[70] = new ModelRendererTurbo(this, 81, 209, textureX, textureY); // Box 183
        body[71] = new ModelRendererTurbo(this, 249, 97, textureX, textureY); // Box 184
        body[72] = new ModelRendererTurbo(this, 345, 121, textureX, textureY); // Box 185
        body[73] = new ModelRendererTurbo(this, 377, 89, textureX, textureY); // Box 187
        body[74] = new ModelRendererTurbo(this, 49, 97, textureX, textureY); // Box 188
        body[75] = new ModelRendererTurbo(this, 337, 209, textureX, textureY); // Box 189
        body[76] = new ModelRendererTurbo(this, 49, 113, textureX, textureY); // Box 196
        body[77] = new ModelRendererTurbo(this, 185, 121, textureX, textureY); // Box 197
        body[78] = new ModelRendererTurbo(this, 105, 225, textureX, textureY); // Box 200
        body[79] = new ModelRendererTurbo(this, 441, 89, textureX, textureY); // Box 203
        body[80] = new ModelRendererTurbo(this, 473, 105, textureX, textureY); // Box 204
        body[81] = new ModelRendererTurbo(this, 113, 97, textureX, textureY); // Box 206
        body[82] = new ModelRendererTurbo(this, 305, 121, textureX, textureY); // Box 207
        body[83] = new ModelRendererTurbo(this, 393, 121, textureX, textureY); // Box 208
        body[84] = new ModelRendererTurbo(this, 137, 129, textureX, textureY); // Box 209
        body[85] = new ModelRendererTurbo(this, 169, 97, textureX, textureY); // Box 210
        body[86] = new ModelRendererTurbo(this, 329, 73, textureX, textureY); // Box 211
        body[87] = new ModelRendererTurbo(this, 433, 73, textureX, textureY); // Box 212
        body[88] = new ModelRendererTurbo(this, 393, 25, textureX, textureY); // Box 213
        body[89] = new ModelRendererTurbo(this, 441, 25, textureX, textureY); // Box 214
        body[90] = new ModelRendererTurbo(this, 193, 249, textureX, textureY); // Box 215
        body[91] = new ModelRendererTurbo(this, 241, 265, textureX, textureY); // Box 216
        body[92] = new ModelRendererTurbo(this, 1, 281, textureX, textureY); // Box 217
        body[93] = new ModelRendererTurbo(this, 1, 297, textureX, textureY); // Box 218
        body[94] = new ModelRendererTurbo(this, 1, 313, textureX, textureY); // Box 219
        body[95] = new ModelRendererTurbo(this, 345, 129, textureX, textureY); // Box 220
        body[96] = new ModelRendererTurbo(this, 297, 241, textureX, textureY); // Box 221
        body[97] = new ModelRendererTurbo(this, 1, 273, textureX, textureY); // Box 222
        body[98] = new ModelRendererTurbo(this, 257, 281, textureX, textureY); // Box 223
        body[99] = new ModelRendererTurbo(this, 265, 289, textureX, textureY); // Box 224
        body[100] = new ModelRendererTurbo(this, 257, 297, textureX, textureY); // Box 225
        body[101] = new ModelRendererTurbo(this, 265, 305, textureX, textureY); // Box 226
        body[102] = new ModelRendererTurbo(this, 305, 137, textureX, textureY); // Box 227
        body[103] = new ModelRendererTurbo(this, 481, 137, textureX, textureY); // Box 228
        body[104] = new ModelRendererTurbo(this, 1, 145, textureX, textureY); // Box 229
        body[105] = new ModelRendererTurbo(this, 385, 281, textureX, textureY); // Box 230
        body[106] = new ModelRendererTurbo(this, 1, 161, textureX, textureY); // Box 262
        body[107] = new ModelRendererTurbo(this, 217, 161, textureX, textureY); // Box 263
        body[108] = new ModelRendererTurbo(this, 433, 161, textureX, textureY); // Box 264
        body[109] = new ModelRendererTurbo(this, 41, 169, textureX, textureY); // Box 265
        body[110] = new ModelRendererTurbo(this, 81, 169, textureX, textureY); // Box 267
        body[111] = new ModelRendererTurbo(this, 257, 169, textureX, textureY); // Box 268
        body[112] = new ModelRendererTurbo(this, 297, 169, textureX, textureY); // Box 269
        body[113] = new ModelRendererTurbo(this, 353, 169, textureX, textureY); // Box 270
        body[114] = new ModelRendererTurbo(this, 49, 121, textureX, textureY); // Box 271
        body[115] = new ModelRendererTurbo(this, 321, 121, textureX, textureY); // Box 272
        body[116] = new ModelRendererTurbo(this, 1, 193, textureX, textureY); // Box 305
        body[117] = new ModelRendererTurbo(this, 433, 193, textureX, textureY); // Box 306
        body[118] = new ModelRendererTurbo(this, 1, 49, textureX, textureY); // Box 307
        body[119] = new ModelRendererTurbo(this, 81, 49, textureX, textureY); // Box 308
        body[120] = new ModelRendererTurbo(this, 145, 49, textureX, textureY); // Box 310
        body[121] = new ModelRendererTurbo(this, 41, 201, textureX, textureY); // Box 313
        body[122] = new ModelRendererTurbo(this, 321, 49, textureX, textureY); // Box 314
        body[123] = new ModelRendererTurbo(this, 481, 153, textureX, textureY); // Box 315
        body[124] = new ModelRendererTurbo(this, 217, 185, textureX, textureY); // Box 316
        body[125] = new ModelRendererTurbo(this, 201, 209, textureX, textureY); // Box 317
        body[126] = new ModelRendererTurbo(this, 369, 209, textureX, textureY); // Box 318
        body[127] = new ModelRendererTurbo(this, 273, 217, textureX, textureY); // Box 319
        body[128] = new ModelRendererTurbo(this, 273, 225, textureX, textureY); // Box 320
        body[129] = new ModelRendererTurbo(this, 457, 145, textureX, textureY); // Box 322
        body[130] = new ModelRendererTurbo(this, 57, 153, textureX, textureY); // Box 323
        body[131] = new ModelRendererTurbo(this, 393, 185, textureX, textureY); // Box 324
        body[132] = new ModelRendererTurbo(this, 393, 193, textureX, textureY); // Box 325
        body[133] = new ModelRendererTurbo(this, 449, 233, textureX, textureY); // Box 326
        body[134] = new ModelRendererTurbo(this, 1, 241, textureX, textureY); // Box 327
        body[135] = new ModelRendererTurbo(this, 65, 241, textureX, textureY); // Box 335
        body[136] = new ModelRendererTurbo(this, 105, 241, textureX, textureY); // Box 336
        body[137] = new ModelRendererTurbo(this, 473, 265, textureX, textureY); // Box 337
        body[138] = new ModelRendererTurbo(this, 441, 281, textureX, textureY); // Box 338
        body[139] = new ModelRendererTurbo(this, 473, 305, textureX, textureY); // Box 349
        body[140] = new ModelRendererTurbo(this, 1, 313, textureX, textureY); // Box 353
        body[141] = new ModelRendererTurbo(this, 297, 201, textureX, textureY); // Box 354
        body[142] = new ModelRendererTurbo(this, 369, 217, textureX, textureY); // Box 356
        body[143] = new ModelRendererTurbo(this, 129, 33, textureX, textureY); // Box 357
        body[144] = new ModelRendererTurbo(this, 73, 97, textureX, textureY); // Box 358
        body[145] = new ModelRendererTurbo(this, 49, 57, textureX, textureY); // Box 361
        body[146] = new ModelRendererTurbo(this, 417, 49, textureX, textureY); // Box 362
        body[147] = new ModelRendererTurbo(this, 49, 65, textureX, textureY); // Box 363
        body[148] = new ModelRendererTurbo(this, 169, 65, textureX, textureY); // Box 366
        body[149] = new ModelRendererTurbo(this, 193, 65, textureX, textureY); // Box 367
        body[150] = new ModelRendererTurbo(this, 449, 73, textureX, textureY); // Box 368
        body[151] = new ModelRendererTurbo(this, 17, 81, textureX, textureY); // Box 370
        body[152] = new ModelRendererTurbo(this, 121, 81, textureX, textureY); // Box 372
        body[153] = new ModelRendererTurbo(this, 169, 81, textureX, textureY); // Box 374
        body[154] = new ModelRendererTurbo(this, 177, 81, textureX, textureY); // Box 375
        body[155] = new ModelRendererTurbo(this, 193, 81, textureX, textureY); // Box 376
        body[156] = new ModelRendererTurbo(this, 289, 89, textureX, textureY); // Box 377
        body[157] = new ModelRendererTurbo(this, 305, 89, textureX, textureY); // Box 378
        body[158] = new ModelRendererTurbo(this, 329, 89, textureX, textureY); // Box 379
        body[159] = new ModelRendererTurbo(this, 473, 89, textureX, textureY); // Box 380
        body[160] = new ModelRendererTurbo(this, 497, 89, textureX, textureY); // Box 381
        body[161] = new ModelRendererTurbo(this, 1, 97, textureX, textureY); // Box 382
        body[162] = new ModelRendererTurbo(this, 409, 169, textureX, textureY); // Box 384
        body[163] = new ModelRendererTurbo(this, 113, 97, textureX, textureY); // Box 385
        body[164] = new ModelRendererTurbo(this, 1, 217, textureX, textureY); // Box 401
        body[165] = new ModelRendererTurbo(this, 17, 177, textureX, textureY); // Box 402
        body[166] = new ModelRendererTurbo(this, 273, 185, textureX, textureY); // Box 419
        body[167] = new ModelRendererTurbo(this, 313, 185, textureX, textureY); // Box 420
        body[168] = new ModelRendererTurbo(this, 137, 129, textureX, textureY); // Box 421
        body[169] = new ModelRendererTurbo(this, 153, 129, textureX, textureY); // Box 422
        body[170] = new ModelRendererTurbo(this, 345, 129, textureX, textureY); // Box 423
        body[171] = new ModelRendererTurbo(this, 73, 121, textureX, textureY); // Box 425
        body[172] = new ModelRendererTurbo(this, 481, 129, textureX, textureY); // Box 426
        body[173] = new ModelRendererTurbo(this, 65, 161, textureX, textureY); // Box 427
        body[174] = new ModelRendererTurbo(this, 177, 137, textureX, textureY); // Box 428
        body[175] = new ModelRendererTurbo(this, 73, 137, textureX, textureY); // Box 429
        body[176] = new ModelRendererTurbo(this, 305, 137, textureX, textureY); // Box 430
        body[177] = new ModelRendererTurbo(this, 505, 137, textureX, textureY); // Box 431
        body[178] = new ModelRendererTurbo(this, 1, 145, textureX, textureY); // Box 432
        body[179] = new ModelRendererTurbo(this, 417, 185, textureX, textureY); // Box 433
        body[180] = new ModelRendererTurbo(this, 25, 145, textureX, textureY); // Box 434
        body[181] = new ModelRendererTurbo(this, 369, 129, textureX, textureY); // Box 435
        body[182] = new ModelRendererTurbo(this, 137, 193, textureX, textureY); // Box 436
        body[183] = new ModelRendererTurbo(this, 273, 193, textureX, textureY); // Box 437
        body[184] = new ModelRendererTurbo(this, 137, 145, textureX, textureY); // Box 438
        body[185] = new ModelRendererTurbo(this, 169, 201, textureX, textureY); // Box 439
        body[186] = new ModelRendererTurbo(this, 201, 201, textureX, textureY); // Box 440

        body[0].addBox(0F, -1.5F, -0.5F, 81, 3, 1, 0F); // Box 0
        body[0].setRotationPoint(-59F, -0.5F, 0F);

        body[1].addShapeBox(0F, 0F, 0F, 2, 4, 12, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 1
        body[1].setRotationPoint(22F, -2F, 11.5F);

        body[2].addShapeBox(0F, 0F, 0F, 2, 4, 12, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, -1F, 0F, -0.5F, -1F, 0F, 0F, 0F, 0F, 0F); // Box 2
        body[2].setRotationPoint(22F, -2F, -23.5F);

        body[3].addShapeBox(0F, 0F, 0F, 2, 4, 12, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, -1F, 0F, -0.5F); // Box 3
        body[3].setRotationPoint(46F, -2F, 11.5F);

        body[4].addShapeBox(0F, 0F, 0F, 2, 4, 12, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, -1F, 0F, 0F); // Box 4
        body[4].setRotationPoint(46F, -2F, -23.5F);

        body[5].addShapeBox(0F, 0F, 0F, 26, 4, 24, 0F, 0F, 0F, 0.5F, 0F, 0F, 0.5F, 0F, 0F, 0.5F, 0F, 0F, 0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 5
        body[5].setRotationPoint(22F, -2F, -12F);

        body[6].addBox(0F, 0F, 0F, 3, 3, 40, 0F); // Box 6
        body[6].setRotationPoint(33.5F, -1.5F, -20F);

        body[7].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F); // Box 7
        body[7].setRotationPoint(-60F, -2F, 13.5F);

        body[8].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F); // Box 8
        body[8].setRotationPoint(-85F, -2F, 13.5F);

        body[9].addShapeBox(0F, 0F, 0F, 26, 4, 28, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 9
        body[9].setRotationPoint(-84F, -2F, -14F);

        body[10].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F); // Box 10
        body[10].setRotationPoint(-60F, -2F, -23.5F);

        body[11].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F); // Box 11
        body[11].setRotationPoint(-85F, -2F, -23.5F);

        body[12].addBox(0F, 0F, 0F, 3, 3, 40, 0F); // Box 100
        body[12].setRotationPoint(-72.5F, -1.5F, -20F);

        body[13].addBox(0F, 0F, 0F, 8, 4, 45, 0F); // Box 101
        body[13].setRotationPoint(48F, -2F, -22.5F);

        body[14].addShapeBox(0F, 0F, 0F, 8, 4, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 102
        body[14].setRotationPoint(48F, -2F, -23.5F);

        body[15].addShapeBox(0F, 0F, 0F, 8, 4, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 103
        body[15].setRotationPoint(48F, -2F, 22.5F);

        body[16].addShapeBox(0F, 0F, 0F, 3, 4, 37, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F); // Box 104
        body[16].setRotationPoint(56F, -2F, -18.5F);

        body[17].addShapeBox(0F, 0F, 0F, 3, 4, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1.5F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F); // Box 105
        body[17].setRotationPoint(56F, -2F, -21.5F);

        body[18].addShapeBox(0F, 0F, 0F, 3, 4, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, -3F, 0F, -0.5F, -1.5F, 0F, 0F, 0F, 0F, 0F); // Box 106
        body[18].setRotationPoint(56F, -2F, -23.5F);

        body[19].addShapeBox(0F, 0F, 0F, 3, 4, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F); // Box 107
        body[19].setRotationPoint(56F, -2F, 18.5F);

        body[20].addShapeBox(0F, 0F, 0F, 3, 4, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1.5F, 0F, 0F, -3F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 108
        body[20].setRotationPoint(56F, -2F, 21.5F);

        body[21].addBox(0F, 0F, 0F, 3, 2, 43, 0F); // Box 109
        body[21].setRotationPoint(56F, -4F, -21.5F);

        body[22].addShapeBox(0F, 0F, 0F, 3, 2, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 110
        body[22].setRotationPoint(56F, -4F, -23.5F);

        body[23].addShapeBox(0F, 0F, 0F, 3, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F); // Box 111
        body[23].setRotationPoint(56F, -4F, 21.5F);

        body[24].addBox(0F, 0F, 0F, 9, 2, 2, 0F); // Box 112
        body[24].setRotationPoint(47F, -4F, -23.5F);

        body[25].addBox(0F, 0F, 0F, 9, 2, 2, 0F); // Box 113
        body[25].setRotationPoint(47F, -4F, 21.5F);

        body[26].addShapeBox(0F, 0F, 0F, 2, 2, 11, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F); // Box 114
        body[26].setRotationPoint(45F, -4F, 12.5F);

        body[27].addShapeBox(0F, 0F, 0F, 2, 2, 11, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F); // Box 115
        body[27].setRotationPoint(45F, -4F, -23.5F);

        body[28].addShapeBox(0F, 0F, 0F, 2, 2, 11, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F); // Box 116
        body[28].setRotationPoint(23F, -4F, 12.5F);

        body[29].addShapeBox(0F, 0F, 0F, 2, 2, 11, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F); // Box 117
        body[29].setRotationPoint(23F, -4F, -23.5F);

        body[30].addBox(0F, 0F, 0F, 24, 7, 1, 0F); // Box 118
        body[30].setRotationPoint(23F, -9F, -12.5F);

        body[31].addBox(0F, 0F, 0F, 24, 7, 1, 0F); // Box 119
        body[31].setRotationPoint(23F, -9F, 11.5F);

        body[32].addShapeBox(0F, 0F, 0F, 7, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -0.5F, 0F, -1F, -0.5F); // Box 121
        body[32].setRotationPoint(47F, -6.5F, 21.5F);

        body[33].addShapeBox(0F, 0F, 0F, 7, 2, 2, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -0.5F, 0F, -1F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 123
        body[33].setRotationPoint(47F, -6.5F, -23.5F);

        body[34].addBox(0F, 0F, 0F, 1, 7, 34, 0F); // Box 129
        body[34].setRotationPoint(55.5F, -13F, -17F);

        body[35].addBox(0F, 0F, 0F, 9, 7, 42, 0F); // Box 139
        body[35].setRotationPoint(47F, -9F, -21F);

        body[36].addBox(0F, 0F, 0F, 24, 7, 23, 0F); // Box 140
        body[36].setRotationPoint(23F, -9F, -11.5F);

        body[37].addShapeBox(0F, 0F, 0F, 4, 4, 2, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F); // Box 142
        body[37].setRotationPoint(54.2F, -13F, 20.8F);

        body[38].addBox(0F, 0F, 0F, 4, 4, 3, 0F); // Box 143
        body[38].setRotationPoint(54.2F, -13F, 17.8F);

        body[39].addBox(0F, 0F, 0F, 4, 4, 3, 0F); // Box 149
        body[39].setRotationPoint(54.2F, -13F, -20.8F);

        body[40].addShapeBox(0F, 0F, 0F, 4, 4, 2, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F); // Box 150
        body[40].setRotationPoint(54.2F, -13F, -22.8F);

        body[41].addBox(0F, 0F, 0F, 1, 2, 47, 0F); // Box 151
        body[41].setRotationPoint(22F, -4F, -23.5F);

        body[42].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 153
        body[42].setRotationPoint(57.4F, -7F, -17F);

        body[43].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 154
        body[43].setRotationPoint(57.4F, -8F, -17F);

        body[44].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 155
        body[44].setRotationPoint(57.4F, -9F, -17F);

        body[45].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 156
        body[45].setRotationPoint(57.4F, -10F, -17F);

        body[46].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 157
        body[46].setRotationPoint(57.4F, -11F, -17F);

        body[47].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 158
        body[47].setRotationPoint(57.4F, -12F, -17F);

        body[48].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 159
        body[48].setRotationPoint(57.4F, -13F, -17F);

        body[49].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 160
        body[49].setRotationPoint(57.4F, -6.65F, -17F);

        body[50].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 161
        body[50].setRotationPoint(57.4F, -7.65F, -17F);

        body[51].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 162
        body[51].setRotationPoint(57.4F, -8.65F, -17F);

        body[52].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 163
        body[52].setRotationPoint(57.4F, -9.65F, -17F);

        body[53].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 164
        body[53].setRotationPoint(57.4F, -10.65F, -17F);

        body[54].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 165
        body[54].setRotationPoint(57.4F, -11.65F, -17F);

        body[55].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 166
        body[55].setRotationPoint(57.4F, -12.65F, -17F);

        body[56].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 167
        body[56].setRotationPoint(57.4F, -7.35F, -17F);

        body[57].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 168
        body[57].setRotationPoint(57.4F, -8.35F, -17F);

        body[58].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 169
        body[58].setRotationPoint(57.4F, -9.35F, -17F);

        body[59].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 170
        body[59].setRotationPoint(57.4F, -10.35F, -17F);

        body[60].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 171
        body[60].setRotationPoint(57.4F, -11.35F, -17F);

        body[61].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 172
        body[61].setRotationPoint(57.4F, -12.35F, -17F);

        body[62].addShapeBox(0F, 0F, 0F, 1, 1, 34, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F); // Box 173
        body[62].setRotationPoint(57.4F, -13.35F, -17F);

        body[63].addShapeBox(0F, 0F, 0F, 1, 7, 1, 0F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F); // Box 174
        body[63].setRotationPoint(57.5F, -13F, -12F);

        body[64].addShapeBox(0F, 0F, 0F, 1, 7, 1, 0F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F); // Box 175
        body[64].setRotationPoint(57.5F, -13F, 11F);

        body[65].addShapeBox(0F, 0F, 0F, 1, 7, 1, 0F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F); // Box 176
        body[65].setRotationPoint(57.5F, -13F, -6F);

        body[66].addShapeBox(0F, 0F, 0F, 1, 7, 1, 0F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F); // Box 177
        body[66].setRotationPoint(57.5F, -13F, 5F);

        body[67].addShapeBox(0F, 0F, 0F, 1, 7, 1, 0F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F, 0F, 0F, -0.4F); // Box 178
        body[67].setRotationPoint(57.5F, -13F, -0.5F);

        body[68].addShapeBox(0F, 0F, 0F, 7, 1, 9, 0F, -0.5F, 0F, 0F, -4F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F); // Box 179
        body[68].setRotationPoint(40F, -10F, 12F);

        body[69].addShapeBox(0F, 0F, 0F, 7, 1, 9, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, -4F, 0F, 0F, -0.5F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F); // Box 182
        body[69].setRotationPoint(40F, -10F, -21F);

        body[70].addShapeBox(0F, 0F, 0F, 7, 1, 24, 0F, 0F, 0F, 0F, -4F, 0F, 0F, -4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 183
        body[70].setRotationPoint(40F, -10F, -12F);

        body[71].addBox(0F, 0F, 0F, 20, 2, 1, 0F); // Box 184
        body[71].setRotationPoint(22F, -11F, -12.5F);

        body[72].addBox(0F, 0F, 0F, 20, 2, 1, 0F); // Box 185
        body[72].setRotationPoint(22F, -11F, 11.5F);

        body[73].addShapeBox(0F, 0F, 0F, 5, 1, 9, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F, 0F, 0F, 0F, 3F, 0F, 0F, -2.5F, 0F, 0F); // Box 187
        body[73].setRotationPoint(38F, -11F, 12F);

        body[74].addShapeBox(0F, 0F, 0F, 5, 1, 9, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F, 3F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F); // Box 188
        body[74].setRotationPoint(38F, -11F, -21F);

        body[75].addShapeBox(0F, 0F, 0F, 3, 1, 24, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 189
        body[75].setRotationPoint(40F, -11F, -12F);

        body[76].addShapeBox(0F, 0F, 0F, 7, 2, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -0.5F, 0F, -1F, -0.5F); // Box 196
        body[76].setRotationPoint(47F, -9F, 21.5F);

        body[77].addShapeBox(0F, 0F, 0F, 7, 2, 2, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -0.5F, 0F, -1F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 197
        body[77].setRotationPoint(47F, -9F, -23.5F);

        body[78].addBox(0F, 0F, 0F, 20, 1, 44, 0F); // Box 200
        body[78].setRotationPoint(22F, -12F, -22F);

        body[79].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, -4F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F); // Box 203
        body[79].setRotationPoint(38F, -11F, 21F);

        body[80].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -4F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F); // Box 204
        body[80].setRotationPoint(38F, -11F, -22F);

        body[81].addShapeBox(0F, 0F, 0F, 1, 5, 7, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 206
        body[81].setRotationPoint(22.5F, 1F, 16F);
        body[81].rotationAngleZ = Static.toDegrees(0.50614548F);

        body[82].addShapeBox(0F, 0F, 0F, 1, 5, 7, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 207
        body[82].setRotationPoint(22.5F, 1F, -23F);
        body[82].rotationAngleZ = Static.toDegrees(0.50614548F);

        body[83].addShapeBox(0F, 0F, 0F, 1, 5, 7, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 208
        body[83].setRotationPoint(-83.5F, 1F, 16F);
        body[83].rotationAngleZ = Static.toDegrees(0.50614548F);

        body[84].addShapeBox(0F, 0F, 0F, 1, 5, 7, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 209
        body[84].setRotationPoint(-83.5F, 1F, -23F);
        body[84].rotationAngleZ = Static.toDegrees(0.50614548F);

        body[85].addBox(0F, 0F, 0F, 3, 1, 3, 0F); // Box 210
        body[85].setRotationPoint(-72.5F, 2F, -1.5F);

        body[86].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 211
        body[86].setRotationPoint(-72.5F, 2F, -2.5F);

        body[87].addShapeBox(0F, 0F, 0F, 3, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F); // Box 212
        body[87].setRotationPoint(-72.5F, 2F, 1.5F);

        body[88].addShapeBox(0F, 0F, 0F, 1, 1, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F); // Box 213
        body[88].setRotationPoint(-73.5F, 2F, -1.5F);

        body[89].addShapeBox(0F, 0F, 0F, 1, 1, 3, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F); // Box 214
        body[89].setRotationPoint(-69.5F, 2F, -1.5F);

        body[90].addBox(0F, 0F, 0F, 120, 5, 3, 0F); // Box 215
        body[90].setRotationPoint(-98F, -6F, -10.5F);

        body[91].addBox(0F, 0F, 0F, 120, 5, 3, 0F); // Box 216
        body[91].setRotationPoint(-98F, -6F, 8.5F);

        body[92].addShapeBox(0F, 0F, 0F, 122, 5, 7, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, -2F, 0F, -2F); // Box 217
        body[92].setRotationPoint(-100F, -11F, -12.5F);

        body[93].addShapeBox(0F, 0F, 0F, 122, 5, 7, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, -2F, 0F, -2F); // Box 218
        body[93].setRotationPoint(-100F, -11F, 6.5F);

        body[94].addShapeBox(0F, 0F, 0F, 124, 1, 45, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F); // Box 219
        body[94].setRotationPoint(-102F, -12F, -22.5F);

        body[95].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F); // Box 220
        body[95].setRotationPoint(-83F, -6F, 13.5F);

        body[96].addBox(0F, 0F, 0F, 81, 1, 1, 0F); // Box 221
        body[96].setRotationPoint(-59F, -1F, -1.5F);

        body[97].addBox(0F, 0F, 0F, 81, 1, 1, 0F); // Box 222
        body[97].setRotationPoint(-59F, -1F, 0.5F);

        body[98].addShapeBox(0F, 0F, 0F, 81, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 223
        body[98].setRotationPoint(-59F, -2F, 0.5F);

        body[99].addShapeBox(0F, 0F, 0F, 81, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 224
        body[99].setRotationPoint(-59F, 0F, -1.5F);

        body[100].addShapeBox(0F, 0F, 0F, 81, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F); // Box 225
        body[100].setRotationPoint(-59F, 0F, 0.5F);

        body[101].addShapeBox(0F, 0F, 0F, 81, 1, 1, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 226
        body[101].setRotationPoint(-59F, -2F, -1.5F);

        body[102].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F); // Box 227
        body[102].setRotationPoint(-62F, -6F, 13.5F);

        body[103].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F); // Box 228
        body[103].setRotationPoint(-83F, -6F, -23.5F);

        body[104].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F); // Box 229
        body[104].setRotationPoint(-62F, -6F, -23.5F);

        body[105].addBox(0F, 0F, 0F, 2, 5, 45, 0F); // Box 230
        body[105].setRotationPoint(-100F, -6F, -21.5F);

        body[106].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, 2F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, 2F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F); // Box 262
        body[106].setRotationPoint(-64F, -10F, 13.5F);

        body[107].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, -3F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, -3F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F); // Box 263
        body[107].setRotationPoint(-81F, -10F, 13.5F);

        body[108].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, -3F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, -3F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F); // Box 264
        body[108].setRotationPoint(-81F, -10F, -23.5F);

        body[109].addShapeBox(0F, 0F, 0F, 3, 4, 10, 0F, 2F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, 2F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F); // Box 265
        body[109].setRotationPoint(-64F, -10F, -23.5F);

        body[110].addShapeBox(0F, 0F, 0F, 3, 1, 10, 0F, 2F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, 2F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F); // Box 267
        body[110].setRotationPoint(-68F, -11F, 13.5F);

        body[111].addShapeBox(0F, 0F, 0F, 3, 1, 10, 0F, -3F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, -3F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F); // Box 268
        body[111].setRotationPoint(-77F, -11F, 13.5F);

        body[112].addShapeBox(0F, 0F, 0F, 3, 1, 10, 0F, -3F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, -3F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F); // Box 269
        body[112].setRotationPoint(-77F, -11F, -23.5F);

        body[113].addShapeBox(0F, 0F, 0F, 3, 1, 10, 0F, 2F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, 2F, 0F, 0F, -2F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, -2F, 0F, 0F); // Box 270
        body[113].setRotationPoint(-68F, -11F, -23.5F);

        body[114].addShapeBox(0F, 0F, 0F, 6, 1, 2, 0F, -2F, -0.5F, 0F, -2F, -0.5F, 0F, -2F, -0.5F, 0F, -2F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 271
        body[114].setRotationPoint(-74F, -12F, 21.5F);

        body[115].addShapeBox(0F, 0F, 0F, 6, 1, 2, 0F, -2F, -0.5F, 0F, -2F, -0.5F, 0F, -2F, -0.5F, 0F, -2F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 272
        body[115].setRotationPoint(-74F, -12F, -23.5F);

        body[116].addBox(0F, 0F, 0F, 3, 11, 9, 0F); // Box 305
        body[116].setRotationPoint(19F, -10.5F, 13.5F);

        body[117].addShapeBox(0F, 0F, 0F, 1, 11, 9, 0F, 0F, -1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -1F, 0F, -1F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -1F); // Box 306
        body[117].setRotationPoint(18F, -10.5F, 13.5F);

        body[118].addBox(0F, 0F, 0F, 1, 1, 1, 0F); // Box 307
        body[118].setRotationPoint(20F, -2.5F, 21.8F);

        body[119].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, -0.25F, -0.75F, 0F, -0.25F, -0.75F, 0F, -0.25F, -0.75F, 0F, -0.25F, -0.75F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 308
        body[119].setRotationPoint(20F, -3.5F, 21.8F);

        body[120].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.25F, -0.75F, 0F, -0.25F, -0.75F, 0F, -0.25F, -0.75F, 0F, -0.25F, -0.75F, 0F); // Box 310
        body[120].setRotationPoint(20F, -1.5F, 21.8F);

        body[121].addBox(0F, 0F, 0F, 4, 10, 8, 0F); // Box 313
        body[121].setRotationPoint(17.9F, -10F, -22F);

        body[122].addBox(0F, 0F, 0F, 1, 2, 1, 0F); // Box 314
        body[122].setRotationPoint(17.5F, -7F, -21.5F);

        body[123].addBox(0F, 0F, 0F, 1, 3, 12, 0F); // Box 315
        body[123].setRotationPoint(58.5F, -2F, -6F);
        body[123].rotationAngleZ = Static.toDegrees(0.26179939F);

        body[124].addBox(0F, 0F, 0F, 1, 3, 12, 0F); // Box 316
        body[124].setRotationPoint(-100.2F, -5F, -6F);
        body[124].rotationAngleX = Static.toDegrees(-0.01745329F);

        body[125].addShapeBox(0F, 0F, 0F, 24, 4, 1, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 317
        body[125].setRotationPoint(-83F, -6F, -14F);

        body[126].addShapeBox(0F, 0F, 0F, 24, 4, 1, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 318
        body[126].setRotationPoint(-83F, -6F, 13F);

        body[127].addShapeBox(0F, 0F, 0F, 20, 4, 1, 0F, -4F, 0F, 0F, -4F, 0F, 0F, -4F, 0F, 0F, -4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 319
        body[127].setRotationPoint(-81F, -10F, 13F);

        body[128].addShapeBox(0F, 0F, 0F, 20, 4, 1, 0F, -4F, 0F, 0F, -4F, 0F, 0F, -4F, 0F, 0F, -4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 320
        body[128].setRotationPoint(-81F, -10F, -14F);

        body[129].addBox(0F, 0F, 0F, 10, 1, 1, 0F); // Box 322
        body[129].setRotationPoint(31F, -13F, -19F);

        body[130].addBox(0F, 0F, 0F, 10, 1, 1, 0F); // Box 323
        body[130].setRotationPoint(31F, -13F, -6F);

        body[131].addBox(0F, 0F, 0F, 10, 1, 1, 0F); // Box 324
        body[131].setRotationPoint(31F, -13F, 16F);

        body[132].addBox(0F, 0F, 0F, 10, 1, 1, 0F); // Box 325
        body[132].setRotationPoint(31F, -13F, 5F);

        body[133].addBox(0F, 0F, 0F, 12, 2, 18, 0F); // Box 326
        body[133].setRotationPoint(30F, -15F, -21F);

        body[134].addBox(0F, 0F, 0F, 12, 2, 16, 0F); // Box 327
        body[134].setRotationPoint(30F, -15F, 3F);

        body[135].addShapeBox(0F, 0F, 0F, 2, 2, 14, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F); // Box 335
        body[135].setRotationPoint(28F, -15F, 4F);

        body[136].addShapeBox(0F, 0F, 0F, 2, 2, 16, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F); // Box 336
        body[136].setRotationPoint(28F, -15F, -20F);

        body[137].addBox(0F, 0F, 0F, 2, 16, 16, 0F); // Box 337
        body[137].setRotationPoint(28F, -31F, 3F);

        body[138].addBox(0F, 0F, 0F, 2, 16, 18, 0F); // Box 338
        body[138].setRotationPoint(28F, -31F, -21F);

        body[139].addShapeBox(0F, 0F, 0F, 5, 6, 14, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 0F, 0F); // Box 349
        body[139].setRotationPoint(50.5F, -24F, 4F);

        body[140].addShapeBox(0F, 0F, 0F, 5, 13, 6, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 1F, 0F, 0F); // Box 353
        body[140].setRotationPoint(50.5F, -22F, -3F);

        body[141].addShapeBox(0F, 0F, 0F, 9, 3, 6, 0F, 0F, 0F, 0F, 0F, 2F, 0F, 0F, 2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 354
        body[141].setRotationPoint(41.5F, -12F, -3F);

        body[142].addBox(0F, 0F, 0F, 1, 4, 10, 0F); // Box 356
        body[142].setRotationPoint(53F, -20F, -17F);

        body[143].addBox(0F, 0F, 0F, 1, 1, 4, 0F); // Box 357
        body[143].setRotationPoint(52.8F, -19.5F, -14F);

        body[144].addShapeBox(0F, 0F, 0F, 1, 2, 2, 0F, 0F, -0.1F, -0.1F, -0.6F, -0.3F, -0.3F, -0.6F, -0.3F, -0.3F, 0F, -0.1F, -0.1F, 0F, -0.1F, -0.1F, -0.6F, -0.3F, -0.3F, -0.6F, -0.3F, -0.3F, 0F, -0.1F, -0.1F); // Box 358
        body[144].setRotationPoint(58.5F, -18F, -1F);

        body[145].addBox(0F, 0F, -1F, 1, 1, 4, 0F); // Box 361
        body[145].setRotationPoint(50.4F, -21.5F, -1F);
        body[145].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[146].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 362
        body[146].setRotationPoint(50.4F, -21.5F, -2.75F);
        body[146].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[147].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 363
        body[147].setRotationPoint(50.4F, -21.5F, 2.25F);
        body[147].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[148].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 366
        body[148].setRotationPoint(50.4F, -23.5F, 17.25F);
        body[148].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[149].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 367
        body[149].setRotationPoint(50.4F, -23.5F, 16.25F);
        body[149].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[150].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 368
        body[150].setRotationPoint(50.4F, -22.5F, 16.25F);
        body[150].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[151].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 370
        body[151].setRotationPoint(50.4F, -21.5F, 17.25F);
        body[151].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[152].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 372
        body[152].setRotationPoint(50.4F, -20.5F, 16.25F);
        body[152].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[153].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 374
        body[153].setRotationPoint(50.4F, -19.5F, 17.25F);
        body[153].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[154].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 375
        body[154].setRotationPoint(50.4F, -19.5F, 16.25F);
        body[154].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[155].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 376
        body[155].setRotationPoint(50.4F, -22.5F, 5.25F);
        body[155].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[156].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 377
        body[156].setRotationPoint(50.4F, -21.5F, 4.25F);
        body[156].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[157].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 378
        body[157].setRotationPoint(50.4F, -21.5F, 5.25F);
        body[157].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[158].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 379
        body[158].setRotationPoint(50.4F, -20.5F, 5.25F);
        body[158].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[159].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 380
        body[159].setRotationPoint(50.4F, -19.5F, 5.25F);
        body[159].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[160].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 381
        body[160].setRotationPoint(50.4F, -20.5F, 4.25F);
        body[160].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[161].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 382
        body[161].setRotationPoint(50.4F, -19.5F, 4.25F);
        body[161].rotationAngleZ = Static.toDegrees(0.10471976F);

        body[162].addBox(0F, 0F, 0F, 5, 2, 2, 0F); // Box 384
        body[162].setRotationPoint(48.5F, -23F, 10F);
        body[162].rotationAngleZ = Static.toDegrees(0.26179939F);

        body[163].addBox(0F, 0F, 0F, 1, 2, 2, 0F); // Box 385
        body[163].setRotationPoint(47.6F, -23F, 10F);

        body[164].addShapeBox(0F, 1F, 0F, 12, 2, 3, 0F, -4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -4F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 401
        body[164].setRotationPoint(28.5F, -15F, -1.5F);

        body[165].addBox(0F, 1F, 0F, 7, 1, 1, 0F); // Box 402
        body[165].setRotationPoint(32.5F, -15F, -0.5F);
        body[165].rotationAngleZ = Static.toDegrees(0.17453293F);

        body[166].addShapeBox(0F, 0F, 0F, 3, 1, 2, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 419
        body[166].setRotationPoint(52.5F, -10F, 8F);

        body[167].addShapeBox(0F, 0F, 0F, 3, 1, 2, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 420
        body[167].setRotationPoint(52.5F, -10F, 13F);

        body[168].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 421
        body[168].setRotationPoint(49.4F, -23.5F, 15F);
        body[168].rotationAngleX = Static.toDegrees(-1.23918377F);
        body[168].rotationAngleZ = Static.toDegrees(0.08726646F);

        body[169].addShapeBox(0F, 0F, 0F, 1, 5, 1, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 422
        body[169].setRotationPoint(49.4F, -23F, 7F);
        body[169].rotationAngleX = Static.toDegrees(1.23918377F);
        body[169].rotationAngleZ = Static.toDegrees(0.08726646F);

        body[170].addBox(0F, 0F, 0F, 1, 5, 1, 0F); // Box 423
        body[170].setRotationPoint(46.5F, -17F, -0.5F);
        body[170].rotationAngleZ = Static.toDegrees(-0.17453293F);

        body[171].addShapeBox(0F, 0F, 0F, 1, 1, 2, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 425
        body[171].setRotationPoint(54.5F, -20F, 23F);

        body[172].addShapeBox(0F, 0F, 0F, 1, 1, 2, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 426
        body[172].setRotationPoint(54.1F, -28F, 23F);

        body[173].addShapeBox(0F, 0F, 0F, 1, 7, 3, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 427
        body[173].setRotationPoint(54.4F, -25F, 23.5F);
        body[173].rotationAngleY = Static.toDegrees(-0.10471976F);

        body[174].addShapeBox(0F, 0F, 0F, 1, 3, 3, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 428
        body[174].setRotationPoint(53.9F, -29F, 23.5F);
        body[174].rotationAngleY = Static.toDegrees(-0.13962634F);
        body[174].rotationAngleZ = Static.toDegrees(-0.12217305F);

        body[175].addShapeBox(0F, 0F, 0F, 1, 1, 2, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 429
        body[175].setRotationPoint(54.5F, -24F, 23F);

        body[176].addShapeBox(0F, 0F, 0F, 1, 1, 2, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 430
        body[176].setRotationPoint(54.5F, -20F, -25F);

        body[177].addShapeBox(0F, 0F, 0F, 1, 1, 2, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 431
        body[177].setRotationPoint(54.5F, -24F, -25F);

        body[178].addShapeBox(0F, 0F, 0F, 1, 1, 2, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 432
        body[178].setRotationPoint(54F, -28F, -25F);

        body[179].addShapeBox(0F, 0F, 0F, 1, 7, 3, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 433
        body[179].setRotationPoint(54.1F, -25F, -26.5F);
        body[179].rotationAngleY = Static.toDegrees(0.10471976F);

        body[180].addShapeBox(0F, 0F, 0F, 1, 3, 3, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 434
        body[180].setRotationPoint(53.4F, -29F, -26.5F);
        body[180].rotationAngleX = Static.toDegrees(-0.01745329F);
        body[180].rotationAngleY = Static.toDegrees(0.13962634F);
        body[180].rotationAngleZ = Static.toDegrees(-0.12217305F);

        body[181].addBox(0F, 0F, 0F, 1, 3, 1, 0F); // Box 435
        body[181].setRotationPoint(-100.5F, -5F, -21F);
        body[181].rotationAngleX = Static.toDegrees(0.01745329F);

        body[182].addBox(0F, 0F, 0F, 1, 2, 6, 0F); // Box 436
        body[182].setRotationPoint(-100.5F, -5F, -19F);
        body[182].rotationAngleX = Static.toDegrees(0.01745329F);

        body[183].addBox(0F, 0F, 0F, 1, 1, 6, 0F); // Box 437
        body[183].setRotationPoint(-100.5F, -2.5F, -19F);
        body[183].rotationAngleX = Static.toDegrees(0.01745329F);

        body[184].addBox(0F, 0F, 0F, 1, 3, 1, 0F); // Box 438
        body[184].setRotationPoint(-100.5F, -5F, 22F);
        body[184].rotationAngleX = Static.toDegrees(0.01745329F);

        body[185].addBox(0F, 0F, 0F, 1, 2, 6, 0F); // Box 439
        body[185].setRotationPoint(-100.5F, -5F, 15F);
        body[185].rotationAngleX = Static.toDegrees(0.01745329F);

        body[186].addBox(0F, 0F, 0F, 1, 1, 6, 0F); // Box 440
        body[186].setRotationPoint(-100.5F, -2.5F, 15F);
        body[186].rotationAngleX = Static.toDegrees(0.01745329F);

        body_door_open_colored_primary = new ModelRendererTurbo[16];
        body_door_open_colored_primary[0] = new ModelRendererTurbo(this, 49, 241, textureX, textureY); // Box 441
        body_door_open_colored_primary[1] = new ModelRendererTurbo(this, 353, 321, textureX, textureY); // Box 442
        body_door_open_colored_primary[2] = new ModelRendererTurbo(this, 297, 329, textureX, textureY); // Box 443
        body_door_open_colored_primary[3] = new ModelRendererTurbo(this, 353, 209, textureX, textureY); // Box 444
        body_door_open_colored_primary[4] = new ModelRendererTurbo(this, 161, 361, textureX, textureY); // Box 445
        body_door_open_colored_primary[5] = new ModelRendererTurbo(this, 321, 217, textureX, textureY); // Box 446
        body_door_open_colored_primary[6] = new ModelRendererTurbo(this, 193, 361, textureX, textureY); // Box 447
        body_door_open_colored_primary[7] = new ModelRendererTurbo(this, 369, 185, textureX, textureY); // Box 448
        body_door_open_colored_primary[8] = new ModelRendererTurbo(this, 89, 241, textureX, textureY); // Box 449
        body_door_open_colored_primary[9] = new ModelRendererTurbo(this, 465, 329, textureX, textureY); // Box 450
        body_door_open_colored_primary[10] = new ModelRendererTurbo(this, 1, 337, textureX, textureY); // Box 451
        body_door_open_colored_primary[11] = new ModelRendererTurbo(this, 169, 193, textureX, textureY); // Box 452
        body_door_open_colored_primary[12] = new ModelRendererTurbo(this, 217, 361, textureX, textureY); // Box 453
        body_door_open_colored_primary[13] = new ModelRendererTurbo(this, 505, 225, textureX, textureY); // Box 454
        body_door_open_colored_primary[14] = new ModelRendererTurbo(this, 249, 361, textureX, textureY); // Box 455
        body_door_open_colored_primary[15] = new ModelRendererTurbo(this, 497, 233, textureX, textureY); // Box 456

        body_door_open_colored_primary[0].addShapeBox(0F, 0F, 0F, 1, 2, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0.5F, 0F, 0F, 0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1.5F, 0F, 0F, -1.5F); // Box 441
        body_door_open_colored_primary[0].setRotationPoint(52.5F, -11F, 22F);

        body_door_open_colored_primary[1].addShapeBox(0F, 0F, 0F, 1, 8, 15, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -2F, 0F, 0F, -2F); // Box 442
        body_door_open_colored_primary[1].setRotationPoint(52.5F, -19F, 21.5F);

        body_door_open_colored_primary[2].addShapeBox(0F, 0F, 0F, 1, 2, 16, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, -1F); // Box 443
        body_door_open_colored_primary[2].setRotationPoint(52.5F, -21F, 21.5F);

        body_door_open_colored_primary[3].addShapeBox(0F, 0F, 0F, 1, 17, 1, 0F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, 3F, 0F, 0F, 3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 444
        body_door_open_colored_primary[3].setRotationPoint(52.5F, -38F, 22F);

        body_door_open_colored_primary[4].addShapeBox(0F, 0F, 0F, 1, 1, 26, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 445
        body_door_open_colored_primary[4].setRotationPoint(52.5F, -22F, 22F);

        body_door_open_colored_primary[5].addBox(0F, 0F, 0F, 1, 15, 1, 0F); // Box 446
        body_door_open_colored_primary[5].setRotationPoint(52.5F, -37F, 47F);

        body_door_open_colored_primary[6].addBox(0F, 0F, 0F, 1, 1, 23, 0F); // Box 447
        body_door_open_colored_primary[6].setRotationPoint(52.5F, -38F, 25F);

        body_door_open_colored_primary[7].addBox(0F, 0F, 0F, 2, 1, 3, 0F); // Box 448
        body_door_open_colored_primary[7].setRotationPoint(52F, -19F, 31.5F);

        body_door_open_colored_primary[8].addShapeBox(0F, 0F, 0F, 1, 2, 8, 0F, 0F, 0F, 0.5F, 0F, 0F, 0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1.5F, 0F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 449
        body_door_open_colored_primary[8].setRotationPoint(52.5F, -11F, -30F);

        body_door_open_colored_primary[9].addShapeBox(0F, 0F, 0F, 1, 8, 15, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 450
        body_door_open_colored_primary[9].setRotationPoint(52.5F, -19F, -36.5F);

        body_door_open_colored_primary[10].addShapeBox(0F, 0F, 0F, 1, 2, 16, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 451
        body_door_open_colored_primary[10].setRotationPoint(52.5F, -21F, -37.5F);

        body_door_open_colored_primary[11].addBox(0F, 0F, 0F, 2, 1, 3, 0F); // Box 452
        body_door_open_colored_primary[11].setRotationPoint(52F, -19F, -34.5F);

        body_door_open_colored_primary[12].addShapeBox(0F, 0F, 0F, 1, 1, 26, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 453
        body_door_open_colored_primary[12].setRotationPoint(52.5F, -22F, -48.5F);

        body_door_open_colored_primary[13].addShapeBox(0F, 0F, 0F, 1, 17, 1, 0F, 0F, 0F, 3F, 0F, 0F, 3F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 454
        body_door_open_colored_primary[13].setRotationPoint(52.5F, -38F, -23F);

        body_door_open_colored_primary[14].addBox(0F, 0F, 0F, 1, 1, 23, 0F); // Box 455
        body_door_open_colored_primary[14].setRotationPoint(52.5F, -38F, -48F);

        body_door_open_colored_primary[15].addBox(0F, 0F, 0F, 1, 15, 1, 0F); // Box 456
        body_door_open_colored_primary[15].setRotationPoint(52.5F, -37F, -48F);

        body_door_close_colored_primary = new ModelRendererTurbo[16];
        body_door_close_colored_primary[0] = new ModelRendererTurbo(this, 121, 225, textureX, textureY); // Box 403
        body_door_close_colored_primary[1] = new ModelRendererTurbo(this, 393, 225, textureX, textureY); // Box 404
        body_door_close_colored_primary[2] = new ModelRendererTurbo(this, 441, 257, textureX, textureY); // Box 405
        body_door_close_colored_primary[3] = new ModelRendererTurbo(this, 1, 265, textureX, textureY); // Box 406
        body_door_close_colored_primary[4] = new ModelRendererTurbo(this, 505, 185, textureX, textureY); // Box 407
        body_door_close_colored_primary[5] = new ModelRendererTurbo(this, 57, 265, textureX, textureY); // Box 408
        body_door_close_colored_primary[6] = new ModelRendererTurbo(this, 329, 129, textureX, textureY); // Box 409
        body_door_close_colored_primary[7] = new ModelRendererTurbo(this, 97, 153, textureX, textureY); // Box 410
        body_door_close_colored_primary[8] = new ModelRendererTurbo(this, 209, 225, textureX, textureY); // Box 411
        body_door_close_colored_primary[9] = new ModelRendererTurbo(this, 297, 313, textureX, textureY); // Box 412
        body_door_close_colored_primary[10] = new ModelRendererTurbo(this, 97, 185, textureX, textureY); // Box 413
        body_door_close_colored_primary[11] = new ModelRendererTurbo(this, 113, 265, textureX, textureY); // Box 414
        body_door_close_colored_primary[12] = new ModelRendererTurbo(this, 169, 273, textureX, textureY); // Box 415
        body_door_close_colored_primary[13] = new ModelRendererTurbo(this, 457, 153, textureX, textureY); // Box 416
        body_door_close_colored_primary[14] = new ModelRendererTurbo(this, 193, 265, textureX, textureY); // Box 417
        body_door_close_colored_primary[15] = new ModelRendererTurbo(this, 97, 209, textureX, textureY); // Box 418

        body_door_close_colored_primary[0].addShapeBox(0F, 0F, 0F, 8, 2, 1, 0F, 0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0.5F, 0F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1.5F, 0F, 0F); // Box 403
        body_door_close_colored_primary[0].setRotationPoint(45.5F, -11F, 22F);

        body_door_close_colored_primary[1].addShapeBox(0F, 0F, 0F, 15, 8, 1, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -2F, 0F, 0F); // Box 404
        body_door_close_colored_primary[1].setRotationPoint(39F, -19F, 22F);

        body_door_close_colored_primary[2].addShapeBox(0F, 0F, 0F, 16, 2, 1, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F); // Box 405
        body_door_close_colored_primary[2].setRotationPoint(38F, -21F, 22F);

        body_door_close_colored_primary[3].addShapeBox(0F, 0F, 0F, 26, 1, 1, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 406
        body_door_close_colored_primary[3].setRotationPoint(27F, -22F, 22F);

        body_door_close_colored_primary[4].addShapeBox(0F, 0F, 0F, 1, 17, 1, 0F, 3F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, 3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 407
        body_door_close_colored_primary[4].setRotationPoint(52.5F, -38F, 22F);

        body_door_close_colored_primary[5].addBox(0F, 0F, 0F, 23, 1, 1, 0F); // Box 408
        body_door_close_colored_primary[5].setRotationPoint(27F, -38F, 22F);

        body_door_close_colored_primary[6].addBox(0F, 0F, 0F, 1, 15, 1, 0F); // Box 409
        body_door_close_colored_primary[6].setRotationPoint(27F, -37F, 22F);

        body_door_close_colored_primary[7].addBox(0F, 0F, 0F, 3, 1, 2, 0F); // Box 410
        body_door_close_colored_primary[7].setRotationPoint(41F, -19F, 21.5F);

        body_door_close_colored_primary[8].addShapeBox(0F, 0F, 0F, 8, 2, 1, 0F, 0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0.5F, 0F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1.5F, 0F, 0F); // Box 411
        body_door_close_colored_primary[8].setRotationPoint(45.5F, -11F, -23F);

        body_door_close_colored_primary[9].addShapeBox(0F, 0F, 0F, 15, 8, 1, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -2F, 0F, 0F); // Box 412
        body_door_close_colored_primary[9].setRotationPoint(39F, -19F, -23F);

        body_door_close_colored_primary[10].addBox(0F, 0F, 0F, 3, 1, 2, 0F); // Box 413
        body_door_close_colored_primary[10].setRotationPoint(41F, -19F, -23.5F);

        body_door_close_colored_primary[11].addShapeBox(0F, 0F, 0F, 16, 2, 1, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F); // Box 414
        body_door_close_colored_primary[11].setRotationPoint(38F, -21F, -23F);

        body_door_close_colored_primary[12].addShapeBox(0F, 0F, 0F, 26, 1, 1, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 415
        body_door_close_colored_primary[12].setRotationPoint(27F, -22F, -23F);

        body_door_close_colored_primary[13].addBox(0F, 0F, 0F, 1, 15, 1, 0F); // Box 416
        body_door_close_colored_primary[13].setRotationPoint(27F, -37F, -23F);

        body_door_close_colored_primary[14].addBox(0F, 0F, 0F, 23, 1, 1, 0F); // Box 417
        body_door_close_colored_primary[14].setRotationPoint(27F, -38F, -23F);

        body_door_close_colored_primary[15].addShapeBox(0F, 0F, 0F, 1, 17, 1, 0F, 3F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, 3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 418
        body_door_close_colored_primary[15].setRotationPoint(52.5F, -38F, -23F);

        body_colored_secondary = new ModelRendererTurbo[1];
        body_colored_secondary[0] = new ModelRendererTurbo(this, 473, 193, textureX, textureY); // Box 312

        body_colored_secondary[0].addBox(0F, 0F, 0F, 4, 11, 9, 0F); // Box 312
        body_colored_secondary[0].setRotationPoint(18F, -10.5F, -22.5F);

        body_colored_primary = new ModelRendererTurbo[63];
        body_colored_primary[0] = new ModelRendererTurbo(this, 33, 73, textureX, textureY); // Box 120
        body_colored_primary[1] = new ModelRendererTurbo(this, 265, 73, textureX, textureY); // Box 122
        body_colored_primary[2] = new ModelRendererTurbo(this, 105, 73, textureX, textureY); // Box 124
        body_colored_primary[3] = new ModelRendererTurbo(this, 153, 73, textureX, textureY); // Box 125
        body_colored_primary[4] = new ModelRendererTurbo(this, 417, 73, textureX, textureY); // Box 126
        body_colored_primary[5] = new ModelRendererTurbo(this, 1, 81, textureX, textureY); // Box 127
        body_colored_primary[6] = new ModelRendererTurbo(this, 89, 81, textureX, textureY); // Box 128
        body_colored_primary[7] = new ModelRendererTurbo(this, 385, 73, textureX, textureY); // Box 130
        body_colored_primary[8] = new ModelRendererTurbo(this, 193, 81, textureX, textureY); // Box 131
        body_colored_primary[9] = new ModelRendererTurbo(this, 281, 81, textureX, textureY); // Box 132
        body_colored_primary[10] = new ModelRendererTurbo(this, 305, 81, textureX, textureY); // Box 133
        body_colored_primary[11] = new ModelRendererTurbo(this, 1, 89, textureX, textureY); // Box 134
        body_colored_primary[12] = new ModelRendererTurbo(this, 305, 81, textureX, textureY); // Box 135
        body_colored_primary[13] = new ModelRendererTurbo(this, 417, 81, textureX, textureY); // Box 136
        body_colored_primary[14] = new ModelRendererTurbo(this, 473, 81, textureX, textureY); // Box 137
        body_colored_primary[15] = new ModelRendererTurbo(this, 1, 89, textureX, textureY); // Box 138
        body_colored_primary[16] = new ModelRendererTurbo(this, 257, 81, textureX, textureY); // Box 141
        body_colored_primary[17] = new ModelRendererTurbo(this, 441, 81, textureX, textureY); // Box 144
        body_colored_primary[18] = new ModelRendererTurbo(this, 209, 89, textureX, textureY); // Box 145
        body_colored_primary[19] = new ModelRendererTurbo(this, 273, 89, textureX, textureY); // Box 146
        body_colored_primary[20] = new ModelRendererTurbo(this, 497, 81, textureX, textureY); // Box 147
        body_colored_primary[21] = new ModelRendererTurbo(this, 97, 89, textureX, textureY); // Box 148
        body_colored_primary[22] = new ModelRendererTurbo(this, 249, 113, textureX, textureY); // Box 152
        body_colored_primary[23] = new ModelRendererTurbo(this, 249, 113, textureX, textureY); // Box 180
        body_colored_primary[24] = new ModelRendererTurbo(this, 465, 113, textureX, textureY); // Box 181
        body_colored_primary[25] = new ModelRendererTurbo(this, 209, 217, textureX, textureY); // Box 186
        body_colored_primary[26] = new ModelRendererTurbo(this, 145, 97, textureX, textureY); // Box 190
        body_colored_primary[27] = new ModelRendererTurbo(this, 193, 97, textureX, textureY); // Box 191
        body_colored_primary[28] = new ModelRendererTurbo(this, 1, 129, textureX, textureY); // Box 192
        body_colored_primary[29] = new ModelRendererTurbo(this, 217, 145, textureX, textureY); // Box 193
        body_colored_primary[30] = new ModelRendererTurbo(this, 305, 73, textureX, textureY); // Box 194
        body_colored_primary[31] = new ModelRendererTurbo(this, 489, 73, textureX, textureY); // Box 195
        body_colored_primary[32] = new ModelRendererTurbo(this, 137, 73, textureX, textureY); // Box 198
        body_colored_primary[33] = new ModelRendererTurbo(this, 289, 73, textureX, textureY); // Box 199
        body_colored_primary[34] = new ModelRendererTurbo(this, 33, 129, textureX, textureY); // Box 201
        body_colored_primary[35] = new ModelRendererTurbo(this, 297, 321, textureX, textureY); // Box 321
        body_colored_primary[36] = new ModelRendererTurbo(this, 353, 337, textureX, textureY); // Box 329
        body_colored_primary[37] = new ModelRendererTurbo(this, 497, 153, textureX, textureY); // Box 331
        body_colored_primary[38] = new ModelRendererTurbo(this, 161, 193, textureX, textureY); // Box 332
        body_colored_primary[39] = new ModelRendererTurbo(this, 17, 193, textureX, textureY); // Box 333
        body_colored_primary[40] = new ModelRendererTurbo(this, 449, 193, textureX, textureY); // Box 334
        body_colored_primary[41] = new ModelRendererTurbo(this, 121, 217, textureX, textureY); // Box 339
        body_colored_primary[42] = new ModelRendererTurbo(this, 473, 217, textureX, textureY); // Box 340
        body_colored_primary[43] = new ModelRendererTurbo(this, 505, 89, textureX, textureY); // Box 341
        body_colored_primary[44] = new ModelRendererTurbo(this, 289, 121, textureX, textureY); // Box 342
        body_colored_primary[45] = new ModelRendererTurbo(this, 337, 201, textureX, textureY); // Box 343
        body_colored_primary[46] = new ModelRendererTurbo(this, 81, 209, textureX, textureY); // Box 344
        body_colored_primary[47] = new ModelRendererTurbo(this, 193, 225, textureX, textureY); // Box 345
        body_colored_primary[48] = new ModelRendererTurbo(this, 1, 233, textureX, textureY); // Box 346
        body_colored_primary[49] = new ModelRendererTurbo(this, 1, 361, textureX, textureY); // Box 347
        body_colored_primary[50] = new ModelRendererTurbo(this, 409, 345, textureX, textureY); // Box 348
        body_colored_primary[51] = new ModelRendererTurbo(this, 113, 361, textureX, textureY); // Box 350
        body_colored_primary[52] = new ModelRendererTurbo(this, 17, 33, textureX, textureY); // Box 351
        body_colored_primary[53] = new ModelRendererTurbo(this, 89, 105, textureX, textureY); // Box 352
        body_colored_primary[54] = new ModelRendererTurbo(this, 497, 257, textureX, textureY); // Box 359
        body_colored_primary[55] = new ModelRendererTurbo(this, 25, 313, textureX, textureY); // Box 360
        body_colored_primary[56] = new ModelRendererTurbo(this, 73, 65, textureX, textureY); // Box 364
        body_colored_primary[57] = new ModelRendererTurbo(this, 145, 65, textureX, textureY); // Box 365
        body_colored_primary[58] = new ModelRendererTurbo(this, 473, 73, textureX, textureY); // Box 369
        body_colored_primary[59] = new ModelRendererTurbo(this, 105, 81, textureX, textureY); // Box 371
        body_colored_primary[60] = new ModelRendererTurbo(this, 153, 81, textureX, textureY); // Box 373
        body_colored_primary[61] = new ModelRendererTurbo(this, 25, 97, textureX, textureY); // Box 383
        body_colored_primary[62] = new ModelRendererTurbo(this, 89, 129, textureX, textureY); // Box 619

        body_colored_primary[0].addShapeBox(0F, 0F, 0F, 7, 7, 1, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 120
        body_colored_primary[0].setRotationPoint(47F, -9F, 20.5F);

        body_colored_primary[1].addShapeBox(0F, 0F, 0F, 7, 7, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 122
        body_colored_primary[1].setRotationPoint(47F, -9F, -21.5F);

        body_colored_primary[2].addBox(0F, 0F, 0F, 1, 5, 11, 0F); // Box 124
        body_colored_primary[2].setRotationPoint(46F, -9F, 12F);

        body_colored_primary[3].addBox(0F, 0F, 0F, 1, 5, 11, 0F); // Box 125
        body_colored_primary[3].setRotationPoint(46F, -9F, -23F);

        body_colored_primary[4].addShapeBox(0F, 0F, 0F, 5, 5, 2, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F); // Box 126
        body_colored_primary[4].setRotationPoint(53.5F, -9F, 21F);

        body_colored_primary[5].addShapeBox(0F, 0F, 0F, 5, 5, 2, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F); // Box 127
        body_colored_primary[5].setRotationPoint(53.5F, -9F, -23F);

        body_colored_primary[6].addBox(0F, 0F, 0F, 3, 2, 42, 0F); // Box 128
        body_colored_primary[6].setRotationPoint(55.5F, -6F, -21F);

        body_colored_primary[7].addShapeBox(0F, 0F, 0F, 3, 3, 5, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F); // Box 130
        body_colored_primary[7].setRotationPoint(55.5F, -9F, -21F);

        body_colored_primary[8].addShapeBox(0F, 0F, 0F, 3, 3, 5, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 131
        body_colored_primary[8].setRotationPoint(55.5F, -9F, 16F);

        body_colored_primary[9].addShapeBox(0F, 0F, 0F, 3, 4, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -0.1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, -0.1F, 0F, 0F); // Box 132
        body_colored_primary[9].setRotationPoint(55.5F, -13F, -18F);

        body_colored_primary[10].addShapeBox(0F, 0F, 0F, 3, 4, 2, 0F, -0.1F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, -0.1F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 133
        body_colored_primary[10].setRotationPoint(55.5F, -13F, 16F);

        body_colored_primary[11].addBox(0F, 0F, 0F, 3, 2, 36, 0F); // Box 134
        body_colored_primary[11].setRotationPoint(55.5F, -15F, -18F);

        body_colored_primary[12].addShapeBox(0F, 0F, 0F, 4, 5, 11, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -3.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -3.5F, 0F, 0F); // Box 135
        body_colored_primary[12].setRotationPoint(42F, -9F, -23F);

        body_colored_primary[13].addShapeBox(0F, 0F, 0F, 4, 5, 11, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -3.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -3.5F, 0F, 0F); // Box 136
        body_colored_primary[13].setRotationPoint(42F, -9F, 12F);

        body_colored_primary[14].addShapeBox(0F, 0F, 0F, 4, 5, 11, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -3.5F, 0F, 0F, -3.5F, 0F, 0F, 0F, 0F, 0F); // Box 137
        body_colored_primary[14].setRotationPoint(24F, -9F, 12F);

        body_colored_primary[15].addShapeBox(0F, 0F, 0F, 4, 5, 11, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -3.5F, 0F, 0F, -3.5F, 0F, 0F, 0F, 0F, 0F); // Box 138
        body_colored_primary[15].setRotationPoint(24F, -9F, -23F);

        body_colored_primary[16].addBox(0F, 0F, 0F, 1, 4, 5, 0F); // Box 141
        body_colored_primary[16].setRotationPoint(54F, -13F, 18F);

        body_colored_primary[17].addShapeBox(0F, 0F, 0F, 5, 2, 2, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F); // Box 144
        body_colored_primary[17].setRotationPoint(53.5F, -15F, 21F);

        body_colored_primary[18].addShapeBox(0F, 0F, 0F, 5, 2, 3, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F); // Box 145
        body_colored_primary[18].setRotationPoint(53.5F, -15F, 18F);

        body_colored_primary[19].addShapeBox(0F, 0F, 0F, 5, 2, 3, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F); // Box 146
        body_colored_primary[19].setRotationPoint(53.5F, -15F, -21F);

        body_colored_primary[20].addShapeBox(0F, 0F, 0F, 5, 2, 2, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F); // Box 147
        body_colored_primary[20].setRotationPoint(53.5F, -15F, -23F);

        body_colored_primary[21].addBox(0F, 0F, 0F, 1, 4, 5, 0F); // Box 148
        body_colored_primary[21].setRotationPoint(54F, -13F, -23F);

        body_colored_primary[22].addBox(0F, 0F, 0F, 2, 5, 46, 0F); // Box 152
        body_colored_primary[22].setRotationPoint(22F, -9F, -23F);

        body_colored_primary[23].addShapeBox(0F, 0F, 0F, 8, 1, 11, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F); // Box 180
        body_colored_primary[23].setRotationPoint(22F, -10F, 12F);

        body_colored_primary[24].addShapeBox(0F, 0F, 0F, 8, 1, 11, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F); // Box 181
        body_colored_primary[24].setRotationPoint(22F, -10F, -23F);

        body_colored_primary[25].addBox(0F, 0F, 0F, 18, 2, 23, 0F); // Box 186
        body_colored_primary[25].setRotationPoint(22F, -11F, -11.5F);

        body_colored_primary[26].addShapeBox(0F, 0F, 0F, 7, 1, 2, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F); // Box 190
        body_colored_primary[26].setRotationPoint(40F, -10F, 21F);

        body_colored_primary[27].addShapeBox(0F, 0F, 0F, 7, 1, 2, 0F, -0.5F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -0.5F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2F, 0F, 0F); // Box 191
        body_colored_primary[27].setRotationPoint(40F, -10F, -23F);

        body_colored_primary[28].addShapeBox(0F, 0F, 0F, 10, 1, 11, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F, -2.5F, 0F, 0F, 0F, 0F, 0F); // Box 192
        body_colored_primary[28].setRotationPoint(22F, -11F, 12F);

        body_colored_primary[29].addShapeBox(0F, 0F, 0F, 10, 1, 11, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F, -2.5F, 0F, 0F, 0F, 0F, 0F); // Box 193
        body_colored_primary[29].setRotationPoint(22F, -11F, -23F);

        body_colored_primary[30].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F); // Box 194
        body_colored_primary[30].setRotationPoint(38F, -11F, 22F);

        body_colored_primary[31].addShapeBox(0F, 0F, 0F, 8, 1, 1, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -2.5F, 0F, 0F); // Box 195
        body_colored_primary[31].setRotationPoint(38F, -11F, -23F);

        body_colored_primary[32].addShapeBox(0F, 0F, 0F, 2, 6, 1, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, -0.5F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, -0.5F); // Box 198
        body_colored_primary[32].setRotationPoint(53.5F, -15F, -18F);

        body_colored_primary[33].addShapeBox(0F, 0F, 0F, 2, 6, 1, 0F, -0.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F); // Box 199
        body_colored_primary[33].setRotationPoint(53.5F, -15F, 17F);

        body_colored_primary[34].addShapeBox(0F, 0F, 0F, 19, 8, 1, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 201
        body_colored_primary[34].setRotationPoint(22F, -19F, -23F);

        body_colored_primary[35].addBox(0F, 0F, 0F, 2, 9, 44, 0F); // Box 321
        body_colored_primary[35].setRotationPoint(22F, -21F, -22F);

        body_colored_primary[36].addBox(0F, 0F, 0F, 5, 6, 42, 0F); // Box 329
        body_colored_primary[36].setRotationPoint(53.5F, -21F, -21F);

        body_colored_primary[37].addShapeBox(0F, 0F, 0F, 1, 6, 5, 0F, 0F, 0F, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0.5F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 331
        body_colored_primary[37].setRotationPoint(53.5F, -15F, 18F);

        body_colored_primary[38].addShapeBox(0F, 0F, 0F, 1, 6, 5, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0.5F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0.5F, 0F, 0F, 0F); // Box 332
        body_colored_primary[38].setRotationPoint(53.5F, -15F, -23F);

        body_colored_primary[39].addShapeBox(0F, 0F, 0F, 5, 6, 2, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F); // Box 333
        body_colored_primary[39].setRotationPoint(53.5F, -21F, 21F);

        body_colored_primary[40].addShapeBox(0F, 0F, 0F, 5, 6, 2, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 334
        body_colored_primary[40].setRotationPoint(53.5F, -21F, -23F);

        body_colored_primary[41].addShapeBox(0F, 0F, 0F, 17, 2, 1, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 339
        body_colored_primary[41].setRotationPoint(22F, -21F, 22F);

        body_colored_primary[42].addShapeBox(0F, 0F, 0F, 17, 2, 1, 0F, 0F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 340
        body_colored_primary[42].setRotationPoint(22F, -21F, -23F);

        body_colored_primary[43].addBox(0F, 0F, 0F, 1, 17, 1, 0F); // Box 341
        body_colored_primary[43].setRotationPoint(52.5F, -26F, 21F);

        body_colored_primary[44].addBox(0F, 0F, 0F, 1, 17, 1, 0F); // Box 342
        body_colored_primary[44].setRotationPoint(52.5F, -26F, -22F);

        body_colored_primary[45].addShapeBox(0F, 0F, 0F, 5, 17, 2, 0F, 3F, 0F, 0F, -3F, 0F, 0F, -4F, 0F, 0F, 3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F); // Box 343
        body_colored_primary[45].setRotationPoint(53.5F, -38F, 21F);

        body_colored_primary[46].addShapeBox(0F, 0F, 0F, 5, 17, 2, 0F, 3F, 0F, 0F, -4F, 0F, 0F, -3F, 0F, 0F, 3F, 0F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 344
        body_colored_primary[46].setRotationPoint(53.5F, -38F, -23F);

        body_colored_primary[47].addShapeBox(0F, 0F, 0F, 5, 17, 1, 0F, 3F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, 3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 345
        body_colored_primary[47].setRotationPoint(53.5F, -38F, -21F);

        body_colored_primary[48].addShapeBox(0F, 0F, 0F, 5, 17, 1, 0F, 3F, 0F, 0F, -3F, 0F, 0F, -3F, 0F, 0F, 3F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 346
        body_colored_primary[48].setRotationPoint(53.5F, -38F, 20F);

        body_colored_primary[49].addBox(0F, 0F, 0F, 32, 1, 46, 0F); // Box 347
        body_colored_primary[49].setRotationPoint(22F, -39F, -23F);

        body_colored_primary[50].addBox(0F, 0F, 0F, 2, 17, 46, 0F); // Box 348
        body_colored_primary[50].setRotationPoint(22F, -38F, -23F);

        body_colored_primary[51].addShapeBox(0F, 0F, 0F, 2, 1, 42, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 350
        body_colored_primary[51].setRotationPoint(54F, -39F, -21F);

        body_colored_primary[52].addShapeBox(0F, 0F, 0F, 2, 1, 2, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -1.5F, 0F, 0F, 0F, 0F, 0F); // Box 351
        body_colored_primary[52].setRotationPoint(54F, -39F, 21F);

        body_colored_primary[53].addShapeBox(0F, 0F, 0F, 2, 1, 2, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -1.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F); // Box 352
        body_colored_primary[53].setRotationPoint(54F, -39F, -23F);

        body_colored_primary[54].addBox(0F, 0F, 0F, 3, 17, 1, 0F); // Box 359
        body_colored_primary[54].setRotationPoint(24F, -38F, -23F);

        body_colored_primary[55].addBox(0F, 0F, 0F, 3, 17, 1, 0F); // Box 360
        body_colored_primary[55].setRotationPoint(24F, -38F, 22F);

        body_colored_primary[56].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 364
        body_colored_primary[56].setRotationPoint(50.4F, -23.5F, 4.25F);
        body_colored_primary[56].rotationAngleZ = Static.toDegrees(0.10471976F);

        body_colored_primary[57].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 365
        body_colored_primary[57].setRotationPoint(50.4F, -23.5F, 5.25F);
        body_colored_primary[57].rotationAngleZ = Static.toDegrees(0.10471976F);

        body_colored_primary[58].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 369
        body_colored_primary[58].setRotationPoint(50.4F, -22.5F, 17.25F);
        body_colored_primary[58].rotationAngleZ = Static.toDegrees(0.10471976F);

        body_colored_primary[59].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 371
        body_colored_primary[59].setRotationPoint(50.4F, -21.5F, 16.25F);
        body_colored_primary[59].rotationAngleZ = Static.toDegrees(0.10471976F);

        body_colored_primary[60].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 373
        body_colored_primary[60].setRotationPoint(50.4F, -20.5F, 17.25F);
        body_colored_primary[60].rotationAngleZ = Static.toDegrees(0.10471976F);

        body_colored_primary[61].addShapeBox(0F, 0F, 0F, 1, 1, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, -0.5F, 0F, 0F, -0.5F); // Box 383
        body_colored_primary[61].setRotationPoint(50.4F, -22.5F, 4.25F);
        body_colored_primary[61].rotationAngleZ = Static.toDegrees(0.10471976F);

        body_colored_primary[62].addShapeBox(0F, 0F, 0F, 19, 8, 1, 0F, 0F, 0F, 0F, -2F, 0F, 0F, -2F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F); // Box 619
        body_colored_primary[62].setRotationPoint(22F, -19F, 22F);

        translate(0F, 0F, 0F);
        //
        for(ModelRendererTurbo turbo : body) turbo.textured = true;
        for(ModelRendererTurbo turbo : body_door_close_colored_primary) turbo.textured = true;
        for(ModelRendererTurbo turbo : body_colored_primary) turbo.textured = true;
        for(ModelRendererTurbo turbo : body_colored_secondary) turbo.textured = true;
    }
    
    //private ModelCompound bodyLines;
    
    @Override
    public void render(){
    	render(body);
    	COLOR.glColorApply();
    	render(body_door_close_colored_primary);
    	render(body_colored_primary);
    	RGB.glColorReset();
    	//
    	RGB.GREEN.glColorApply();
    	render(body_colored_secondary);
    	RGB.glColorReset();
    	/*if(bodyLines == null){
    		bodyLines = new ModelCompound(body);
    		bodyLines.lines = true;
    	} bodyLines.render();*/
    }

	public void render(ModelRendererTurbo[] model){
		for(ModelRendererTurbo turbo : model) turbo.render();
	}

	@Override
	public void translate(float x, float y, float z){
		//
	}

	@Override
	public void rotate(float x, float y, float z){
		//
	}

}
