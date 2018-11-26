# Name:  Ryan Myers, Andrew Brown
# Section:  CPE 315-01
# Description:  This program plots both lines and circles
                given a set of coordinates and shape parameters

.text

main:
    # init stack pointer to 0 for pixels
    addi    $sp, $0, 0
    #head
    addi    $a0, $0, 30
    addi    $a1, $0, 100
    addi    $a2, $0, 20
    jal     circle
    #body
    addi    $a0, $0, 30
    addi    $a1, $0, 80
    addi    $a2, $0, 30
    addi    $a3, $0, 30
    jal     line
    #left leg
    addi    $a0, $0, 20
    addi    $a1, $0, 1
    addi    $a2, $0, 30
    addi    $a3, $0, 30
    jal     line
    #right leg
    addi    $a0, $0, 40
    addi    $a1, $0, 1
    addi    $a2, $0, 30
    addi    $a3, $0, 30
    jal     line
    #left arm
    addi    $a0, $0, 15
    addi    $a1, $0, 60
    addi    $a2, $0, 30
    addi    $a3, $0, 50
    jal     line
    #right arm
    addi    $a0, $0, 30
    addi    $a1, $0, 50
    addi    $a2, $0, 45
    addi    $a3, $0, 60
    jal     line
    #left eye
    addi    $a0, $0, 24
    addi    $a1, $0, 105
    addi    $a2, $0, 3
    jal     circle
    #right eye
    addi    $a0, $0, 36
    addi    $a1, $0, 105
    addi    $a2, $0, 3
    jal     circle
    #mouth center
    addi    $a0, $0, 25
    addi    $a1, $0, 90
    addi    $a2, $0, 35
    addi    $a3, $0, 90
    jal     line
    #mouth left
    addi    $a0, $0, 25
    addi    $a1, $0, 90
    addi    $a2, $0, 20
    addi    $a3, $0, 95
    jal     line
    #mouth right
    addi    $a0, $0, 35
    addi    $a1, $0, 90
    addi    $a2, $0, 40
    addi    $a3, $0, 95
    jal     line
    j       endprog




line:
    sub     $t0, $a3, $a1
    sub     $t1, $a2, $a0
    sub     $t3, $t0, $t1
    slt     $t3, $t3, $0
    beq     $t3, $0, xgty
    # if (y1 - y0) > (x1 - x0)
    addi    $t4, $0, 1
    j       chkst
xgty:
    addi    $t4, $0, 0
    # $t7 now contains st

chkst:
    beq     $t4, $0, xcmp
    add     $t0, $0, $a0
    add     $a0, $0, $a1
    add     $a1, $0, $t0
    add     $t0, $0, $a2
    add     $a2, $0, $a3
    add     $a3, $0, $t0

xcmp:
    slt     $t0, $a2, $a0
    beq     $t0, $0, delta
    add     $t0, $0, $a0
    add     $a0, $0, $a2
    add     $a2, $0, $t0
    add     $t0, $0, $a1
    add     $a1, $0, $a3
    add     $a3, $0, $t0

    # set deltax, deltay, error, y
    sub     $t5, $a2, $a0
    # $t5 now has deltax
    sub     $t6, $a3, $a1
    # $t6 now has deltay
    addi    $t7, $0, 0
    # $t7 now has error
    add     $t8, $0, $a1
    # $t8 now has y

    # set counter reg to use in later loop, use $t4
    add     $t4, $0, $a0

    # set ystep
    slt     $t0, $a1, $a3
    beq     $t0, $0, setneg
    addi    $t9, $0, 1
    j       plotl
setneg:
    addi    $t9, $0, -1
    # $t9 now has ystep

plotl:
    slt     $t3, $t4, $a2
    beq     $t3, $0, endl

    beq     $t7, $0, pxy
    # plot(y,x)
    sw      $t8, 0($sp)
    sw      $t4, 4($sp)
    addi    $t4, $t4, 1
    j       stpl
pxy:
    sw      $t4, 0($sp)
    sw      $t8, 0($sp)
stpl:
    addi    $sp, $sp, 8
    add     $t7, $t7, $t6
    add     $t0, $t7, $t7
    slt     $t0, $t0, $t5
    beq     $t0, $0, nostep
    add     $t8, $t8, $t9
    sub     $t7, $t7, $t5
nostep:
    j       plotl
endl:
    jr      $ra

circle:

endprog: