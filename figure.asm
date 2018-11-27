# Name:  Ryan Myers, Andrew Brown
# Section:  CPE 315-01
# Description:  This program plots both lines and circles
#               given a set of coordinates and shape parameters

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



# def Line(x0, y0, x1, y1)
    #x0=$a0, y0=$a1, x1=$a2, y1=$a3
    #st=$t0, deltax=$t1, deltay=$t2, error=$t3
    #y=$t4, ystep=$t5
    #rest of random temps=$t6-$t9
line:
        #abs(y1-y0) -> $t6
        sub $t6, $a3, $a1
        slt $t7, $t6, $0
        beq $t7, $0, pos1
        sub $t6, $a1, $a3
    pos1:
        #abs(x1-x0) -> $t7
        sub $t7, $a2, $a0
        slt $t8, $t7, $0
        beq $t8, $0, pos2
        sub $t7, $a0, $a2
    pos2:
        #if abs(y1-y0) > abs(x1-x0)
        slt $t8, $t7, $t6
        bne $t8, $0, set_st_1
        addi $t0, $0, 0
        j check_st
    set_st_1:
        addi $t0, $0, 1
    check_st:
        #if st==1
        beq $t0, $0, cmp_x
        #swap(x0,y0)
        add $t6, $0, $a0
        add $a0, $0, $a1
        add $a1, $0, $t6
        #swap(x1,y1)
        add $t6, $0, $a2
        add $a2, $0, $a3
        add $a3, $0, $t6
    #if x0 > x1
    cmp_x:
        slt $t6, $a2, $a0
        beq $t6, $0, set_delta
        #swap x0, x1
        add $t6, $a2, $0
        add $a2, $0, $a0
        add $a0, $0, $t6
        #swap y0, y1
        add $t6, $a1, $0
        add $a1, $0, $a3
        add $a3, $0, $t6
    set_delta:
        #deltax = x1-x0
        sub $t1, $a2, $a0
        #deltay = abs(y1-y0)
        sub $t2, $a3, $a1
        slt $t7, $t2, $0
        beq $t7, $0, cmp_y
        sub $t2, $a1, $a3
    cmp_y:
        #error=0, y=y0
        add $t3, $0, $0
        add $t4, $0, $a1
        #if y0<y1
        slt $t6, $a1, $a3
        addi $t5, $0, -1
        beq $t6, $0, set_counter
        addi $t5, $0, 1
    set_counter:
        add $t6, $0, $a0
    inner_loop:
            #if x1 < counter -> break
            slt $t7, $a2, $t6
            bne $t7, $0, end_line
            #if st == 0, plot x,y else plot(y,x)
            beq $t0, $0, plot_xy
            # plot(y,x)
            sw      $t4, 0($sp)
            sw      $t6, 1($sp)
            addi    $sp, $sp, 2
            j error_stuff
        plot_xy:
            sw      $t6, 0($sp)
            sw      $t4, 1($sp)
            addi    $sp, $sp, 2
        error_stuff:
            add $t3, $t3, $t2
            #if deltax < 2*error
            add $t7, $0, $t3
            add $t7, $t7, $t3
            slt $t8, $t1, $t7
            beq $t8, $0, incr_x
            add $t4, $t4, $t5
            sub $t3, $t3, $t1
        incr_x:
            addi $t6, $t6, 1
            j inner_loop
    end_line:
        jr $ra

circle:
        addi    $t5, $0, 0
        # $t5 now contains x
        add     $t6, $0, $a2
        # $t6 now contains y
        addi    $t7, $0, 3
        sub     $t7, $t7, $a2
        sub     $t7, $t7, $a2
        # $t7 now contains g
        addi    $t8, $0, 10
        sub     $t8, $t8, $a2
        sub     $t8, $t8, $a2
        sub     $t8, $t8, $a2
        sub     $t8, $t8, $a2
        # $t8 now contains diagonalInc
        addi    $t9, $0, 6
        # $t9 now contains rightInc

    loopc:
        slt     $t0, $t6, $t5
        bne     $t0, $0, endc
        # plot(xc+x,yc+y)
        add     $t1, $a0, $t5
        add     $t2, $a1, $t6
        sw      $t1, 0($sp)
        sw      $t2, 1($sp)
        addi    $sp, $sp, 2
        # plot(xc+x,yc-y)
        add     $t1, $a0, $t5
        sub     $t2, $a1, $t6
        sw      $t1, 0($sp)
        sw      $t2, 1($sp)
        addi    $sp, $sp, 2
        # plot(xc-x,yc+y)
        sub     $t1, $a0, $t5
        add     $t2, $a1, $t6
        sw      $t1, 0($sp)
        sw      $t2, 1($sp)
        addi    $sp, $sp, 2
        # plot(xc-x,yc-y)
        sub     $t1, $a0, $t5
        sub     $t2, $a1, $t6
        sw      $t1, 0($sp)
        sw      $t2, 1($sp)
        addi    $sp, $sp, 2
        # plot(xc+y,yc+x)
        add     $t1, $a0, $t6
        add     $t2, $a1, $t5
        sw      $t1, 0($sp)
        sw      $t2, 1($sp)
        addi    $sp, $sp, 2
        # plot(xc+y,yc-x)
        add     $t1, $a0, $t6
        sub     $t2, $a1, $t5
        sw      $t1, 0($sp)
        sw      $t2, 1($sp)
        addi    $sp, $sp, 2
        # plot(xc-y,yc+x)
        sub     $t1, $a0, $t6
        add     $t2, $a1, $t5
        sw      $t1, 0($sp)
        sw      $t2, 1($sp)
        addi    $sp, $sp, 2
        # plot(xc-y,yc-x)
        sub     $t1, $a0, $t6
        sub     $t2, $a1, $t5
        sw      $t1, 0($sp)
        sw      $t2, 1($sp)
        addi    $sp, $sp, 2
        # conditional thing
        slt     $t0, $t7, $0
        bne     $t0, $0, elsec
        add     $t7, $t7, $t8
        addi    $t8, $t8, 8
        addi    $t6, $t6, -1
        j       rinc
    elsec:
        add     $t7, $t7, $t9
        addi    $t8, $t8, 4
    rinc:
        addi    $t9, $t9, 4
        addi    $t5, $t5, 1
        j       loopc

    endc:
        jr      $ra


endprog:
    #dummy instruction
    add $0, $0, $0
